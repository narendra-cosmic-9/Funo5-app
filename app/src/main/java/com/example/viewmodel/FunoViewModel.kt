package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class ChatMessage(
    val id: String,
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

class FunoViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val favoriteRepository = FavoriteRepository(db.favoriteDao())

    // All 1000 tools loaded once in memory for ultra-fast client-side filtering
    val allTools: List<Tool> = ToolDatabaseGenerator.generateAllTools()

    // Search & Filter state
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    private val _selectedPricing = MutableStateFlow<String?>(null) // null = All, "Free", "Freemium"
    val selectedPricing: StateFlow<String?> = _selectedPricing.asStateFlow()

    fun selectPricing(pricing: String?) {
        _selectedPricing.value = pricing
    }

    // Persistent Search History
    val recentSearchHistory: StateFlow<List<SearchHistoryEntity>> = db.favoriteDao().getRecentSearchHistory()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun saveSearchQuery(query: String) {
        val trimmed = query.trim()
        if (trimmed.isBlank()) return
        viewModelScope.launch {
            db.favoriteDao().insertSearchQuery(SearchHistoryEntity(trimmed))
        }
    }

    fun deleteSearchQuery(query: String) {
        viewModelScope.launch {
            db.favoriteDao().deleteSearchQuery(query)
        }
    }

    fun clearAllSearchHistory() {
        viewModelScope.launch {
            db.favoriteDao().clearAllSearchHistory()
        }
    }

    // Selected tool for the detail view modal/dialog
    private val _selectedTool = MutableStateFlow<Tool?>(null)
    val selectedTool: StateFlow<Tool?> = _selectedTool.asStateFlow()

    // Bookmark/Favorites list from Room
    val favorites: StateFlow<List<FavoriteToolEntity>> = favoriteRepository.allFavorites
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Chat bot state
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(listOf(
        ChatMessage(
            id = "welcome",
            text = "Hello! I am Funo, your premium AI Tool Assistant. I can recommend any of the 1000 tools in our catalog, prioritizing the 25 absolute best 'Few Picks' in Video, Website, Photo, Code, and Prompts. What are you looking to create today?",
            isUser = false
        )
    ))
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _isTyping = MutableStateFlow(false)
    val isTyping: StateFlow<Boolean> = _isTyping.asStateFlow()

    // Check if the Gemini API key is configured
    val isApiKeyConfigured: Boolean by lazy {
        val key = getApiKey()
        key.isNotBlank() && key != "MY_GEMINI_API_KEY" && key != "placeholder"
    }

    private fun getApiKey(): String {
        return try {
            val clazz = Class.forName("com.example.BuildConfig")
            val field = clazz.getField("GEMINI_API_KEY")
            field.get(null) as String
        } catch (e: Exception) {
            ""
        }
    }

    // Filtered tools computed efficiently in memory on query, category or pricing change
    val filteredTools: StateFlow<List<Tool>> = combine(_searchQuery, _selectedCategory, _selectedPricing) { query, category, pricing ->
        var list = allTools
        if (category != null) {
            list = list.filter { it.category == category }
        }
        if (pricing != null) {
            list = list.filter { it.pricing.equals(pricing, ignoreCase = true) }
        }
        if (query.isNotBlank()) {
            val q = query.trim().lowercase()
            list = list.filter {
                it.name.lowercase().contains(q) ||
                it.bestFor.lowercase().contains(q) ||
                it.tags.any { tag -> tag.lowercase().contains(q) }
            }
        }
        list
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = allTools
    )

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectCategory(category: String?) {
        _selectedCategory.value = category
    }

    fun selectTool(tool: Tool?) {
        _selectedTool.value = tool
    }

    // Bookmark Operations
    fun toggleFavorite(toolId: String) {
        viewModelScope.launch {
            val isFav = favorites.value.any { it.toolId == toolId }
            if (isFav) {
                favoriteRepository.removeFavorite(toolId)
            } else {
                favoriteRepository.addFavorite(toolId)
            }
        }
    }

    fun isFavorite(toolId: String): Boolean {
        return favorites.value.any { it.toolId == toolId }
    }

    // Chat bot send message logic
    fun sendMessage(text: String) {
        if (text.isBlank()) return
        
        // Add user message
        val userMsg = ChatMessage(id = "user_${System.currentTimeMillis()}", text = text, isUser = true)
        _chatMessages.update { it + userMsg }

        // Trigger bot response
        _isTyping.value = true
        viewModelScope.launch {
            val apiKey = getApiKey()
            if (isApiKeyConfigured && apiKey.isNotBlank()) {
                // Real Gemini API Call (Direct REST)
                try {
                    val systemInstruction = "You are Funo, the elite AI Concierge for FUNO5, the premium AI Tool Hub. " +
                            "Your mission is to recommend the best AI tools based on the user's needs. " +
                            "We have 25 handpicked FEATURED AI tools (5 per category) in our catalog which are premium recommendations: " +
                            "1. Video: OpusClip (best long-to-viral-clip), CapCut AI (best multi-editor), Runway (best text-to-video), Synthesia (best AI presenter avatars), Pictory (best blog-to-video script). " +
                            "2. Website: Hostinger AI (best instant builder), Framer AI (best design & layouts), Wix ADI (best automated wix), Durable (best 30s site), Relume (best design system wireframing). " +
                            "3. Photo: Adobe Firefly (best commercially safe vector/design), Photoroom (best ecommerce backdrop removal), Remove.bg (best fast background eraser), Midjourney (best creative illustrations & art), Canva AI (best social designs & templates). " +
                            "4. Code: Cursor (best pairing code editor), Bolt.new (best in-browser web app creation), v0.dev (best React & Tailwind generation), GitHub Copilot (best helper extension), Replit Agent (best python/backend creator). " +
                            "5. Prompt: PromptPerfect (best prompt optimizer), PromptHero (best Midjourney/SD discovery), FlowGPT (best ChatGPT community playground), PromptBase (best prompt marketplace), ChatX (best prompt generator). " +
                            "Whenever a user asks for tools, you MUST prioritize recommending our featured tools first with detailed descriptions, ratings, and monthly users. " +
                            "If the user wants alternative, niche, or free options, you can recommend tools from our 1000 AI tools list or other popular software. " +
                            "Format your response beautifully with simple markdown bullets and clear paragraphs. Be modern, helpful, and concise."

                    // Compile conversation history
                    val contents = _chatMessages.value.takeLast(10).map { msg ->
                        Content(parts = listOf(Part(text = msg.text)))
                    }

                    val request = GeminiRequest(
                        contents = contents,
                        systemInstruction = Content(parts = listOf(Part(text = systemInstruction)))
                    )

                    val response = withContext(Dispatchers.IO) {
                        GeminiClient.api.generateContent(apiKey, request)
                    }

                    val responseText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                        ?: "I apologize, but I am having trouble forming a response right now. Please try again."

                    _chatMessages.update {
                        it + ChatMessage(
                            id = "bot_${System.currentTimeMillis()}",
                            text = responseText,
                            isUser = false
                        )
                    }
                } catch (e: Exception) {
                    // Fall back to smart offline recommendations on network error
                    val offlineText = getSmartOfflineResponse(text)
                    _chatMessages.update {
                        it + ChatMessage(
                            id = "bot_${System.currentTimeMillis()}",
                            text = "*(Offline Mode)* $offlineText",
                            isUser = false
                        )
                    }
                } finally {
                    _isTyping.value = false
                }
            } else {
                // Key not configured, use ultra-smart offline rules engine instantly
                withContext(Dispatchers.Default) {
                    kotlinx.coroutines.delay(1000) // Realistic typing feel
                }
                val offlineText = getSmartOfflineResponse(text)
                _chatMessages.update {
                    it + ChatMessage(
                        id = "bot_${System.currentTimeMillis()}",
                        text = offlineText,
                        isUser = false
                    )
                }
                _isTyping.value = false
            }
        }
    }

    // Highly intelligent offline keyword search and recommendation database
    private fun getSmartOfflineResponse(userPrompt: String): String {
        val q = userPrompt.lowercase()
        return when {
            q.contains("video") || q.contains("clip") || q.contains("short") || q.contains("edit") || q.contains("avatar") -> {
                "Best is **OpusClip** (4.8★, 8.5M users) for long-to-viral clipping, and **CapCut AI** (4.7★, 15M users) for direct multi-editor utilities. " +
                        "However, if you want more niche video generators or custom solutions from our 1000 AI tools database, I highly recommend checking out: " +
                        "\n\n1. **Runway** (4.9★) — Elite text-to-video generative AI." +
                        "\n2. **Synthesia** (4.6★) — Enterprise-grade talking avatars." +
                        "\n3. **Pictory** (4.5★) — Turns articles and scripts into social clips." +
                        "\n\nYou can click on the Video sidebar category to see these top 5 picks in detail, along with 195 other video tools!"
            }
            q.contains("web") || q.contains("site") || q.contains("builder") || q.contains("framer") || q.contains("host") -> {
                "Best is **Hostinger AI** (4.7★, 3.5M users) for auto-generating responsive hosts, and **Framer AI** (4.8★, 5.1M users) for absolute design flexibility. " +
                        "Alternatively, you can explore other highly-rated website tools from our catalog:" +
                        "\n\n1. **Wix ADI** (4.4★) — Simple, prompt-guided personal builders." +
                        "\n2. **Durable** (4.5★) — Generate a business site with fully customized layout and copies in under 30 seconds." +
                        "\n3. **Relume** (4.6★) — Advanced sitemaps and Figma wireframing design-systems." +
                        "\n\nWould you like me to find a specific website layout tool for you?"
            }
            q.contains("photo") || q.contains("image") || q.contains("art") || q.contains("remove") || q.contains("bg") || q.contains("background") -> {
                "Best is **Adobe Firefly** (4.8★, 9.2M users) for commercially safe asset designs, and **Photoroom** (4.7★, 12M users) for e-commerce studio shots. " +
                        "If you need specialized image utilities, try: " +
                        "\n\n1. **Remove.bg** (4.6★, 14M users) — The fastest automatic background removal." +
                        "\n2. **Midjourney** (4.9★, 7.5M users) — The gold standard for hyper-realistic artistic illustration." +
                        "\n3. **Canva AI** (4.7★, 15M users) — Graphic templates with smart auto-layouts." +
                        "\n\nYou can click on the Photo sidebar category to search all 200 image tools!"
            }
            q.contains("code") || q.contains("develop") || q.contains("program") || q.contains("app") || q.contains("software") || q.contains("react") || q.contains("html") -> {
                "Best is **Cursor** (4.9★, 3.2M users) for AI pairing, and **Bolt.new** (4.8★, 2.4M users) for instant full-stack in-browser deployments. " +
                        "Other premium developer agents in our database are:" +
                        "\n\n1. **v0.dev** (4.8★, 2.9M users) — Generate fully customized React + Tailwind layouts from prompts." +
                        "\n2. **GitHub Copilot** (4.7★, 10M users) — Seamless editor autocompletes." +
                        "\n3. **Replit Agent** (4.6★, 1.5M users) — Rapid full-stack application creator." +
                        "\n\nWhich language or framework are you planning to use?"
            }
            q.contains("prompt") || q.contains("engineering") || q.contains("chatgpt") || q.contains("optimize") -> {
                "Best is **PromptPerfect** (4.7★, 1.1M users) for instant prompt optimization across LLMs, and **PromptHero** (4.5★) for stable diffusion prompts. " +
                        "For community databases or custom prompt markets:" +
                        "\n\n1. **FlowGPT** (4.6★) — Share, chat, and fork conversational prompts." +
                        "\n2. **PromptBase** (4.4★) — Sell and buy premium prompt blueprints." +
                        "\n3. **ChatX** (4.3★) — Interactive free generator of custom role-play templates." +
                        "\n\nYou can select the Prompt sidebar category to browse 200 curated prompts!"
            }
            else -> {
                "I can recommend the absolute best AI systems in **Video**, **Website**, **Photo**, **Code**, or **Prompts**. " +
                        "For example, if you ask 'I want to build an app', I will recommend **Cursor** or **Bolt.new**. If you say 'I need viral shorts', I will show you **OpusClip**. " +
                        "What is your project goal for today?"
            }
        }
    }
}

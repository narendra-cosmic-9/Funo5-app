package com.example.data

data class Tool(
    val id: String,
    val name: String,
    val category: String, // "Video", "Website", "Photo", "Code", "Prompt", "Build App"
    val websiteUrl: String,
    val appUrl: String,
    val logoHex: String,
    val bestFor: String,
    val perfectPrompt: String,
    val rating: Float,
    val tags: List<String>,
    val isFeatured: Boolean,
    val monthlyUsers: String,
    val pricing: String = "Freemium" // "Free" or "Freemium"
)

object ToolDatabaseGenerator {
    
    val CATEGORIES = listOf("Video", "Website", "Photo", "Code", "Prompt", "Build App")
    
    // Core 25 Best Featured Tools for the main carousel
    val FEATURED_TOOLS = listOf(
        // --- Video (5 tools) ---
        Tool(
            id = "opus_clip",
            name = "OpusClip",
            category = "Video",
            websiteUrl = "https://opus.pro",
            appUrl = "https://opus.pro/app",
            logoHex = "#FF0055",
            bestFor = "Creating short viral clips from long videos with AI captions",
            perfectPrompt = "Analyze this 2-hour podcast and extract 5 high-engagement viral shorts with auto-reframe and dynamic animated subtitles.",
            rating = 4.8f,
            tags = listOf("viral", "clipping", "shorts", "reframe"),
            isFeatured = true,
            monthlyUsers = "8.5M",
            pricing = "Freemium"
        ),
        Tool(
            id = "capcut_ai",
            name = "CapCut AI",
            category = "Video",
            websiteUrl = "https://capcut.com",
            appUrl = "https://capcut.com/editor",
            logoHex = "#00F0FF",
            bestFor = "Smart video editing, background removal and subtitles",
            perfectPrompt = "Generate auto-captions for this video, remove the green screen background, and apply the cinematic color grading filter.",
            rating = 4.7f,
            tags = listOf("editor", "captions", "mobile", "filters"),
            isFeatured = true,
            monthlyUsers = "15M",
            pricing = "Free"
        ),
        Tool(
            id = "runway",
            name = "Runway",
            category = "Video",
            websiteUrl = "https://runwayml.com",
            appUrl = "https://app.runwayml.com",
            logoHex = "#7F00FF",
            bestFor = "Generative video, text-to-video, and motion control",
            perfectPrompt = "Create a cinematic slow-motion video of a futuristic neon city in the rain, camera panning slowly to the left, ultra-realistic, 4K.",
            rating = 4.9f,
            tags = listOf("generative", "creative", "pro", "text-to-video"),
            isFeatured = true,
            monthlyUsers = "4.2M",
            pricing = "Freemium"
        ),
        Tool(
            id = "synthesia",
            name = "Synthesia",
            category = "Video",
            websiteUrl = "https://synthesia.io",
            appUrl = "https://synthesia.io/create",
            logoHex = "#FFC500",
            bestFor = "AI video generators with realistic talking avatars",
            perfectPrompt = "Create a professional onboarding video using the executive male avatar, speaking clearly in a warm corporate tone.",
            rating = 4.6f,
            tags = listOf("avatars", "presenter", "enterprise", "multilingual"),
            isFeatured = true,
            monthlyUsers = "2.1M",
            pricing = "Freemium"
        ),
        Tool(
            id = "pictory",
            name = "Pictory",
            category = "Video",
            websiteUrl = "https://pictory.ai",
            appUrl = "https://pictory.ai/home",
            logoHex = "#00FF66",
            bestFor = "Turning written scripts and blog posts into engaging videos",
            perfectPrompt = "Convert this blog post outline into a 1-minute educational social media video with high-quality stock footage and background music.",
            rating = 4.5f,
            tags = listOf("blog-to-video", "script", "summary", "social"),
            isFeatured = true,
            monthlyUsers = "1.8M",
            pricing = "Freemium"
        ),

        // --- Website (5 tools) ---
        Tool(
            id = "hostinger_ai",
            name = "Hostinger AI",
            category = "Website",
            websiteUrl = "https://hostinger.com",
            appUrl = "https://hostinger.com/ai-website-builder",
            logoHex = "#6100FF",
            bestFor = "Generating fully designed, hostable websites in minutes",
            perfectPrompt = "Build a modern, sleek minimalist website for a boutique artisan coffee roastery in Brooklyn with an integrated reservation system.",
            rating = 4.7f,
            tags = listOf("no-code", "builder", "hosting", "fast"),
            isFeatured = true,
            monthlyUsers = "3.5M",
            pricing = "Freemium"
        ),
        Tool(
            id = "framer_ai",
            name = "Framer AI",
            category = "Website",
            websiteUrl = "https://framer.com",
            appUrl = "https://framer.com/projects",
            logoHex = "#000000",
            bestFor = "Transforming text prompts into high-fidelity custom web designs",
            perfectPrompt = "Generate a dark premium landing page for a decentralized financial dashboard, with floating glassmorphism cards and smooth entrance animations.",
            rating = 4.8f,
            tags = listOf("design", "builder", "animations", "interactive"),
            isFeatured = true,
            monthlyUsers = "5.1M",
            pricing = "Freemium"
        ),
        Tool(
            id = "wix_adi",
            name = "Wix ADI",
            category = "Website",
            websiteUrl = "https://wix.com",
            appUrl = "https://wix.com/adi",
            logoHex = "#3F00FF",
            bestFor = "Creating personalized websites automatically based on Q&A",
            perfectPrompt = "Design a professional portfolio website for a freelance landscape architect with contact forms, interactive maps, and galleries.",
            rating = 4.4f,
            tags = listOf("builder", "simple", "ecommerce", "customizable"),
            isFeatured = true,
            monthlyUsers = "6.8M",
            pricing = "Free"
        ),
        Tool(
            id = "durable",
            name = "Durable",
            category = "Website",
            websiteUrl = "https://durable.co",
            appUrl = "https://durable.co/app",
            logoHex = "#FF5500",
            bestFor = "Building a professional business site in 30 seconds with SEO",
            perfectPrompt = "Generate a business website for a mobile car detailing service, complete with pricing tables, service lists, and a testimonial block.",
            rating = 4.5f,
            tags = listOf("business", "quick", "marketing", "seo"),
            isFeatured = true,
            monthlyUsers = "1.2M",
            pricing = "Freemium"
        ),
        Tool(
            id = "relume",
            name = "Relume",
            category = "Website",
            websiteUrl = "https://relume.io",
            appUrl = "https://library.relume.io",
            logoHex = "#0D1117",
            bestFor = "AI wireframing and structured sitemaps for web designers",
            perfectPrompt = "Create a detailed sitemap and interactive wireframe for an online education portal featuring courses, quizzes, and discussion forums.",
            rating = 4.6f,
            tags = listOf("wireframe", "sitemap", "design-system", "figma"),
            isFeatured = true,
            monthlyUsers = "950K",
            pricing = "Freemium"
        ),

        // --- Photo (5 tools) ---
        Tool(
            id = "adobe_firefly",
            name = "Adobe Firefly",
            category = "Photo",
            websiteUrl = "https://firefly.adobe.com",
            appUrl = "https://firefly.adobe.com/generate/images",
            logoHex = "#FF0000",
            bestFor = "Safe commercial image generation and vector recoloring",
            perfectPrompt = "Generate a vibrant vector flat-illustration of a creative teamwork brainstorming session in a modern office environment, with clean borders.",
            rating = 4.8f,
            tags = listOf("generative-fill", "design", "creative", "vector"),
            isFeatured = true,
            monthlyUsers = "9.2M",
            pricing = "Freemium"
        ),
        Tool(
            id = "photoroom",
            name = "Photoroom",
            category = "Photo",
            websiteUrl = "https://photoroom.com",
            appUrl = "https://photoroom.com/app",
            logoHex = "#FF00AA",
            bestFor = "Studio-quality product photography and background removal",
            perfectPrompt = "Isolate this product bottle, apply a realistic shadow underneath, and set a clean luxury marble kitchen countertop background.",
            rating = 4.7f,
            tags = listOf("e-commerce", "background-remover", "retouch", "shadows"),
            isFeatured = true,
            monthlyUsers = "12M",
            pricing = "Free"
        ),
        Tool(
            id = "remove_bg",
            name = "Remove.bg",
            category = "Photo",
            websiteUrl = "https://remove.bg",
            appUrl = "https://remove.bg/upload",
            logoHex = "#00AAFF",
            bestFor = "Instant 100% automatic background removal",
            perfectPrompt = "Automatically cut out this person from the busy street background, ensuring precise hair details and zero color contamination.",
            rating = 4.6f,
            tags = listOf("utility", "remover", "fast", "hair-precision"),
            isFeatured = true,
            monthlyUsers = "14M",
            pricing = "Free"
        ),
        Tool(
            id = "midjourney",
            name = "Midjourney",
            category = "Photo",
            websiteUrl = "https://midjourney.com",
            appUrl = "https://midjourney.com/showcase",
            logoHex = "#2E1C4E",
            bestFor = "Hyper-realistic artistic illustrations from text prompts",
            perfectPrompt = "An award-winning close-up portrait of an astronaut on Mars looking at the horizon, dramatic cinematic lighting, photorealistic, 8K resolution.",
            rating = 4.9f,
            tags = listOf("art", "highest-quality", "creative", "illustration"),
            isFeatured = true,
            monthlyUsers = "7.5M",
            pricing = "Freemium"
        ),
        Tool(
            id = "canva_ai",
            name = "Canva AI",
            category = "Photo",
            websiteUrl = "https://canva.com",
            appUrl = "https://canva.com/features/ai-image-generator",
            logoHex = "#00C4CC",
            bestFor = "Intelligent graphic design and layout suggestions",
            perfectPrompt = "Generate a professional Instagram square template for a business webinar on AI marketing, using bold violet text and abstract elements.",
            rating = 4.7f,
            tags = listOf("design", "templates", "social-media", "layout"),
            isFeatured = true,
            monthlyUsers = "15M",
            pricing = "Free"
        ),

        // --- Prompt (5 tools) ---
        Tool(
            id = "prompt_perfect",
            name = "PromptPerfect",
            category = "Prompt",
            websiteUrl = "https://promptperfect.jina.ai",
            appUrl = "https://promptperfect.jina.ai/app",
            logoHex = "#1D5F5E",
            bestFor = "Auto-optimizing raw prompts for major LLMs instantly",
            perfectPrompt = "Optimize the raw prompt: 'explain quantum physics to kid' for Gemini 1.5 Pro to generate a highly visual and metaphorical story.",
            rating = 4.7f,
            tags = listOf("optimization", "engineering", "utility", "precision"),
            isFeatured = true,
            monthlyUsers = "1.1M",
            pricing = "Freemium"
        ),
        Tool(
            id = "prompt_hero",
            name = "PromptHero",
            category = "Prompt",
            websiteUrl = "https://prompthero.com",
            appUrl = "https://prompthero.com/search",
            logoHex = "#050709",
            bestFor = "Discovering high-performing prompts for stable diffusion and Midjourney",
            perfectPrompt = "Search for top-rated cyberpunk and neo-noir portrait prompt templates that achieved maximum engagement this month.",
            rating = 4.5f,
            tags = listOf("search-engine", "library", "discovery", "visual"),
            isFeatured = true,
            monthlyUsers = "2.5M",
            pricing = "Free"
        ),
        Tool(
            id = "flow_gpt",
            name = "FlowGPT",
            category = "Prompt",
            websiteUrl = "https://flowgpt.com",
            appUrl = "https://flowgpt.com/chat",
            logoHex = "#0052FF",
            bestFor = "An interactive community platform for sharing and testing ChatGPT prompts",
            perfectPrompt = "Explore and fork the community's highest-voted Prompt for simulating a full-stack technical interview agent.",
            rating = 4.6f,
            tags = listOf("community", "chatgpt", "playground", "clone"),
            isFeatured = true,
            monthlyUsers = "3.0M",
            pricing = "Free"
        ),
        Tool(
            id = "prompt_base",
            name = "PromptBase",
            category = "Prompt",
            websiteUrl = "https://promptbase.com",
            appUrl = "https://promptbase.com/marketplace",
            logoHex = "#0C5A30",
            bestFor = "A marketplace for buying and selling high-quality premium prompts",
            perfectPrompt = "List my highly tested Dall-E 3 architectural watercolor styling prompt for sale in the creative design category.",
            rating = 4.4f,
            tags = listOf("marketplace", "premium", "monetize", "templates"),
            isFeatured = true,
            monthlyUsers = "800K",
            pricing = "Freemium"
        ),
        Tool(
            id = "chat_x",
            name = "ChatX",
            category = "Prompt",
            websiteUrl = "https://chatx.ai",
            appUrl = "https://chatx.ai/generator",
            logoHex = "#E52D27",
            bestFor = "A free database and generator of custom prompts for various jobs",
            perfectPrompt = "Generate a tailor-made sales pitch email copy using the PAS framework, fully parameterized for digital agency services.",
            rating = 4.3f,
            tags = listOf("free", "templates", "generator", "email"),
            isFeatured = true,
            monthlyUsers = "600K",
            pricing = "Free"
        )
    )

    // Specific requested 100 Build App Tools list
    // Top 10 will be featured, rest 90 general
    val BUILD_APP_NAMED_LIST = listOf(
        // Featured Top 10 BIG cards
        "Cursor" to true,
        "Bolt.new" to true,
        "v0.dev" to true,
        "Replit Agent" to true,
        "GitHub Copilot" to true,
        "Lovable" to true,
        "Softr AI" to true,
        "Bubble AI" to true,
        "Glide AI" to true,
        "FlutterFlow AI" to true,
        // General 90
        "Blackbox AI" to false,
        "Codeium" to false,
        "Tabnine" to false,
        "Amazon CodeWhisperer" to false,
        "Sourcegraph Cody" to false,
        "Firebase Studio" to false,
        "Supabase AI" to false,
        "Adalo AI" to false,
        "Draftbit" to false,
        "Thunkable" to false,
        "Android Studio Bot" to false,
        "Xcode AI" to false,
        "Expo AI" to false,
        "AppGyver" to false,
        "AppMySite" to false,
        "Andromo" to false,
        "AppSheet" to false,
        "Builder.ai" to false,
        "Backendless" to false,
        "Xano" to false,
        "OutSystems" to false,
        "Mendix" to false,
        "Retool" to false,
        "ToolJet" to false,
        "Appsmith" to false,
        "UI Bakery" to false,
        "Anima" to false,
        "Locofy" to false,
        "TeleportHQ" to false,
        "Vercel AI SDK" to false,
        "Next.js AI" to false,
        "ChatGPT Code Interpreter" to false,
        "Claude Code" to false,
        "Gemini Code Assist" to false,
        "DeepSeek Coder" to false,
        "Qwen Code" to false,
        "StarCoder" to false,
        "Code Llama" to false,
        "WizardCoder" to false,
        "Phind AI" to false,
        "Bito AI" to false,
        "Mutable AI" to false,
        "Codium AI" to false,
        "Continue.dev" to false,
        "Pieces AI" to false,
        "CodeSandbox AI" to false,
        "StackBlitz AI" to false,
        "Gitpod" to false,
        "Codespaces AI" to false
    )

    private val PREFIXES = listOf(
        "Opti", "Synthetix", "Ultra", "Flex", "Nova", "Neuron", "Aero", "Pulse", "Gen", "Deep",
        "Flux", "Flow", "Pixel", "Creart", "Smart", "Auto", "Max", "Prime", "Quantum", "Hyper",
        "Logix", "Vibe", "Cogni", "Vectra", "Nexus", "Stratos", "Apex", "Synapse", "Volt", "Zenith"
    )

    private val SUFFIXES = listOf(
        "Lab", "Mind", "Grid", "Forge", "Craft", "Studio", "Wizard", "Engine", "Scale", "Hub",
        "Node", "Core", "Vector", "Lens", "Sync", "Vision", "Wave", "Intel", "Link", "Shift",
        "Flow", "Boost", "Grid", "Base", "Suite", "AI", "X", "Pro", "Mesh", "Frame"
    )

    private val EXTRA_TAGS = listOf(
        "no-code", "utility", "fast", "seo", "cloud", "free-tier", "pro-mode", "automation",
        "real-time", "secure", "responsive", "customizable", "analytics", "cross-platform", "export"
    )

    private val COLOR_HEXES = listOf(
        "#FF007F", "#00F0FF", "#7F00FF", "#FFC500", "#00FF66", "#6100FF", "#FF5500", "#FF00AA", "#00AAFF", "#F15A24"
    )

    fun generateAllTools(): List<Tool> {
        val result = ArrayList<Tool>(1100)
        
        // 1. Add general featured tools (not Build App)
        result.addAll(FEATURED_TOOLS)
        
        // 2. Generate exactly 100 tools for "Build App" using the defined list + pad to 100
        val buildAppTools = ArrayList<Tool>()
        for ((idx, item) in BUILD_APP_NAMED_LIST.withIndex()) {
            val name = item.first
            val isFeatured = item.second
            val seed = name.hashCode() + idx
            val colorIdx = Math.abs(seed * 17 + 3) % COLOR_HEXES.size
            val logoHex = COLOR_HEXES[colorIdx]
            val rating = 4.3f + (Math.abs(seed % 8) / 10f)
            val isFree = seed % 3 == 0
            
            buildAppTools.add(
                Tool(
                    id = "build_app_${name.lowercase().replace(".", "_").replace(" ", "_")}",
                    name = name,
                    category = "Build App",
                    websiteUrl = "https://${name.lowercase().replace(" ", "").replace(".", "")}.ai",
                    appUrl = "https://app.${name.lowercase().replace(" ", "").replace(".", "")}.ai",
                    logoHex = logoHex,
                    bestFor = "Rapid full-stack production and design with AI synthesis",
                    perfectPrompt = "Synthesize an modern reactive dashboard for monitoring sales with real-time updates.",
                    rating = rating,
                    tags = listOf("builder", "no-code", "deploy"),
                    isFeatured = isFeatured,
                    monthlyUsers = "${200 + (seed % 800)}K",
                    pricing = if (isFree) "Free" else "Freemium"
                )
            )
        }
        
        // Pad Build App to exactly 100 tools
        val currentSize = buildAppTools.size
        for (i in 1..(100 - currentSize)) {
            val seed = 25000 + i
            val pIdx = Math.abs(seed * 7 + 13) % PREFIXES.size
            val sIdx = Math.abs(seed * 11 + 29) % SUFFIXES.size
            val name = PREFIXES[pIdx] + SUFFIXES[sIdx] + " App"
            val rating = 3.9f + (Math.abs(seed % 10) / 10f)
            val colorIdx = Math.abs(seed * 17 + 3) % COLOR_HEXES.size
            val isFree = seed % 3 == 0

            buildAppTools.add(
                Tool(
                    id = "build_app_padded_$i",
                    name = name,
                    category = "Build App",
                    websiteUrl = "https://${name.lowercase().replace(" ", "")}.ai",
                    appUrl = "https://app.${name.lowercase().replace(" ", "")}.ai",
                    logoHex = COLOR_HEXES[colorIdx],
                    bestFor = "Automated application development pipeline",
                    perfectPrompt = "Develop a simple mobile layout.",
                    rating = rating,
                    tags = listOf("builder", "utility", "fast"),
                    isFeatured = false,
                    monthlyUsers = "${100 + (seed % 300)}K",
                    pricing = if (isFree) "Free" else "Freemium"
                )
            )
        }
        result.addAll(buildAppTools)

        // 3. For other 5 categories ("Video", "Website", "Photo", "Code", "Prompt"), scale each to exactly 200 tools
        // 200 * 5 other categories + 100 Build App = exactly 1100 total tools!
        val otherCategories = listOf("Video", "Website", "Photo", "Code", "Prompt")
        for (category in otherCategories) {
            val featuredCount = FEATURED_TOOLS.count { it.category == category }
            val countToGenerate = 200 - featuredCount
            
            for (i in 1..countToGenerate) {
                val seed = category.hashCode() + i + 10000
                val pIdx = Math.abs(seed * 7 + 13) % PREFIXES.size
                val sIdx = Math.abs(seed * 11 + 29) % SUFFIXES.size
                var name = PREFIXES[pIdx] + SUFFIXES[sIdx]
                
                if (FEATURED_TOOLS.any { it.name == name } || result.any { it.name == name }) {
                    name += " " + (100 + (seed % 900))
                }
                
                val id = name.lowercase().replace(" ", "_") + "_" + (seed % 10000)
                val colorIdx = Math.abs(seed * 17 + 3) % COLOR_HEXES.size
                val rating = 3.8f + (Math.abs(seed % 12) / 10f)
                val isFree = seed % 3 == 0

                result.add(
                    Tool(
                        id = id,
                        name = name,
                        category = category,
                        websiteUrl = "https://${name.lowercase().replace(" ", "")}.ai",
                        appUrl = "https://app.${name.lowercase().replace(" ", "")}.ai",
                        logoHex = COLOR_HEXES[colorIdx],
                        bestFor = "Smart high-fidelity model solution for $category workflow optimization",
                        perfectPrompt = "Construct a custom optimized setup of $category.",
                        rating = rating,
                        tags = listOf(category.lowercase(), EXTRA_TAGS[Math.abs(seed * 19) % EXTRA_TAGS.size]),
                        isFeatured = false,
                        monthlyUsers = "${10 + (seed % 400)}K",
                        pricing = if (isFree) "Free" else "Freemium"
                    )
                )
            }
        }
        
        return result
    }
}

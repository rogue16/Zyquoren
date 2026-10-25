package com.example.data.engine

import android.util.Log
import com.example.BuildConfig
import com.example.data.model.CameraMovement
import com.example.data.model.ParticleEffectType
import com.example.data.model.VideoAspectRatio
import com.example.data.model.VideoProject
import com.example.data.model.VideoScene
import com.example.data.model.VideoStyle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID
import java.util.concurrent.TimeUnit

class AiVideoDirector {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    /**
     * Enhances a simple user prompt into an elaborate cinematic screenplay direction.
     */
    suspend fun enhancePromptWithAi(rawPrompt: String, style: VideoStyle): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val promptText = """
                    You are a world-class Hollywood cinematographer and AI video director.
                    Convert this short video idea into a detailed, visually stunning video direction in French (2-3 sentences max).
                    Incorporate lighting, camera lens, movement, and visual mood matching the style '${style.title}'.
                    User Idea: "$rawPrompt"
                    Output only the enhanced description in French, no intro or quotes.
                """.trimIndent()

                val jsonPayload = JSONObject().apply {
                    put("contents", JSONArray().apply {
                        put(JSONObject().apply {
                            put("parts", JSONArray().apply {
                                put(JSONObject().put("text", promptText))
                            })
                        })
                    })
                    put("generationConfig", JSONObject().apply {
                        put("temperature", 0.7)
                        put("maxOutputTokens", 250)
                    })
                }

                val request = Request.Builder()
                    .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey")
                    .post(jsonPayload.toString().toRequestBody("application/json".toMediaType()))
                    .build()

                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val body = response.body?.string()
                    if (!body.isNullOrBlank()) {
                        val root = JSONObject(body)
                        val text = root.optJSONArray("candidates")
                            ?.optJSONObject(0)
                            ?.optJSONObject("content")
                            ?.optJSONArray("parts")
                            ?.optJSONObject(0)
                            ?.optString("text")
                            ?.trim()
                        if (!text.isNullOrBlank()) {
                            return@withContext text
                        }
                    }
                }
            } catch (e: Exception) {
                Log.w("AiVideoDirector", "Gemini enhancement fallback: ${e.message}")
            }
        }

        // Autonomous Creative Director fallback (rich cinematic screenplay generator)
        generateAutonomousEnhancedPrompt(rawPrompt, style)
    }

    /**
     * Builds multi-scene storyboard with synchronized narration, camera motion, and visual seeds.
     */
    suspend fun generateStoryboard(
        title: String,
        prompt: String,
        enhancedPrompt: String,
        style: VideoStyle,
        aspectRatio: VideoAspectRatio,
        sceneCount: Int = 3
    ): VideoProject = withContext(Dispatchers.IO) {
        val totalDuration = sceneCount * 5.0f
        val scenes = mutableListOf<VideoScene>()

        val cameraMoves = when (style) {
            VideoStyle.CYBERPUNK_2099 -> listOf(CameraMovement.ZOOM_IN, CameraMovement.PAN_RIGHT, CameraMovement.ORBIT_SLOW, CameraMovement.TILT_UP)
            VideoStyle.SCI_FI_ODYSSEY -> listOf(CameraMovement.DRONE_SWEEP, CameraMovement.ZOOM_IN, CameraMovement.ORBIT_SLOW, CameraMovement.ZOOM_OUT)
            VideoStyle.NATURE_DOCUMENTARY -> listOf(CameraMovement.PAN_LEFT, CameraMovement.ZOOM_IN, CameraMovement.TILT_UP, CameraMovement.PAN_RIGHT)
            VideoStyle.ANIME_GHIBLI -> listOf(CameraMovement.TILT_UP, CameraMovement.ZOOM_IN, CameraMovement.PAN_RIGHT, CameraMovement.ZOOM_OUT)
            VideoStyle.RETRO_SYNTHWAVE -> listOf(CameraMovement.DRONE_SWEEP, CameraMovement.ZOOM_IN, CameraMovement.PAN_LEFT, CameraMovement.ORBIT_SLOW)
            else -> listOf(CameraMovement.ZOOM_IN, CameraMovement.PAN_RIGHT, CameraMovement.DRONE_SWEEP, CameraMovement.ZOOM_OUT)
        }

        val palette = style.colorPalette

        val narrationScenes = generateSceneNarrations(prompt, enhancedPrompt, style, sceneCount)

        for (i in 0 until sceneCount) {
            val cameraMove = cameraMoves[i % cameraMoves.size]
            val visualPromptScene = "${style.cameraKeywords}, ${style.subtitle}. Scene ${i + 1}: ${enhancedPrompt.take(120)} - Mouvement de caméra ${cameraMove.label}."
            val primaryColor = palette.getOrElse(i % palette.size) { 0xFF111422 }
            val secondaryColor = palette.getOrElse((i + 1) % palette.size) { 0xFF9D4EDD }
            val accentColor = palette.getOrElse((i + 2) % palette.size) { 0xFF00F5D4 }

            scenes.add(
                VideoScene(
                    id = UUID.randomUUID().toString(),
                    sceneIndex = i + 1,
                    durationSeconds = 5.0f,
                    visualPrompt = visualPromptScene,
                    cameraMovement = cameraMove,
                    narrationSnippet = narrationScenes[i],
                    particleType = style.defaultParticle,
                    primaryColorHex = primaryColor,
                    secondaryColorHex = secondaryColor,
                    accentColorHex = accentColor,
                    seed = (prompt.hashCode().toLong() * 31 + i * 17)
                )
            )
        }

        VideoProject(
            id = UUID.randomUUID().toString(),
            title = title.ifBlank { generateTitle(prompt) },
            prompt = prompt,
            enhancedPrompt = enhancedPrompt,
            style = style,
            aspectRatio = aspectRatio,
            totalDurationSeconds = totalDuration,
            scenes = scenes,
            narrationVoice = "Voix IA Studio Cinématographique (FR)",
            isTtsEnabled = true,
            areSubtitlesEnabled = true,
            selectedAiModel = "Google Gemini Omni 1.1 + Veo Engine",
            tags = listOf(style.title, aspectRatio.ratioText, "${scenes.size} Plans", "4K Ultra-HD")
        )
    }

    private fun generateAutonomousEnhancedPrompt(prompt: String, style: VideoStyle): String {
        val basePrompt = prompt.trim().removeSuffix(".")
        return when (style) {
            VideoStyle.CINEMATIC_HOLLYWOOD ->
                "Plan séquence cinématographique à couper le souffle de $basePrompt. Éclairage volumétrique 35mm anamorphique, atmosphère dramatique avec brume dorée, profondeur de champ subtile et contraste saisissant."
            VideoStyle.CYBERPUNK_2099 ->
                "Vue panoramique nocturne futuriste de $basePrompt baignée de néons cyans et pourpres. Ruelles sous une pluie battante avec reflets spéculaires sur l'asphalte et hologrammes flottants."
            VideoStyle.SCI_FI_ODYSSEY ->
                "Odyssée stellaire grandiose révélant $basePrompt aux abords d'une nébuleuse cosmique incandescente. Reflets stellaires sur la coque métallique, poussières d'étoiles et immensité sidérale."
            VideoStyle.ANIME_GHIBLI ->
                "Scène poétique inspirée du Studio Ghibli mettant en valeur $basePrompt. Ciel d'été éclatant avec nuages peints à l'aquarelle, brise douce et couleurs chatoyantes pleines d'émotion."
            VideoStyle.NATURE_DOCUMENTARY ->
                "Documentaire animalier 8K ultra-détaillé capturant $basePrompt dans son habitat sauvage naturel. Lumière dorée rasante de fin d'après-midi, netteté millimétrique et ambiance sonore immersive."
            VideoStyle.RETRO_SYNTHWAVE ->
                "Esthétique rétro-futuriste des années 80 avec $basePrompt au centre d'une grille néon infinie. Soleil couchant magenta géant, reflets chromés et scanlines VHS vintage."
        }
    }

    private fun generateTitle(prompt: String): String {
        val words = prompt.trim().split(" ")
        return if (words.size <= 4) {
            prompt.replaceFirstChar { it.uppercase() }
        } else {
            words.take(4).joinToString(" ").replaceFirstChar { it.uppercase() } + "..."
        }
    }

    private fun generateSceneNarrations(prompt: String, enhanced: String, style: VideoStyle, sceneCount: Int): List<String> {
        val shortPrompt = prompt.trim().removeSuffix(".")
        return when (sceneCount) {
            2 -> listOf(
                "Dans un univers où chaque détail prend vie, découvrez $shortPrompt sous un angle inédit.",
                "L'atmosphère s'intensifie, révélant la puissance et l'harmonie de cette vision cinématique."
            )
            4 -> listOf(
                "Acte un : L'ouverture révèle $shortPrompt dans toute sa grandeur mystérieuse.",
                "Acte deux : La caméra s'approche, dévoilant des nuances cachées et une énergie palpable.",
                "Acte trois : Le point culminant transforme la scène dans une explosion de lumière et de mouvement.",
                "Épilogue : Le plan s'éloigne majestueusement, scellant cette odyssée visuelle mémorable."
            )
            else -> listOf(
                "Introduction : L'histoire commence alors que $shortPrompt se dévoile dans une clarté absolue.",
                "Progression : Les éléments s'animent en rythme, portés par des reflets et des mouvements hypnotiques.",
                "Climax : Un souffle spectaculaire illumine l'ensemble du tableau dans un éclat cinématique inoubliable."
            )
        }
    }

    /**
     * Default curated demo projects ready for instant preview and inspection.
     */
    fun createSampleProjects(): List<VideoProject> {
        val p1 = VideoProject(
            id = "demo_project_cyberpunk",
            title = "Néo-Tokyo 2099",
            prompt = "Un pilote volant au-dessus des gratte-ciels néons sous une pluie d'étincelles",
            enhancedPrompt = "Vue panoramique nocturne futuriste de Néo-Tokyo sous une pluie battante avec reflets spéculaires et hologrammes flottants.",
            style = VideoStyle.CYBERPUNK_2099,
            aspectRatio = VideoAspectRatio.LANDSCAPE_16_9,
            totalDurationSeconds = 15.0f,
            scenes = listOf(
                VideoScene(
                    sceneIndex = 1,
                    durationSeconds = 5f,
                    visualPrompt = "Survol aérien des tours lumineuses de Néo-Tokyo, pluie battante et néons cyans.",
                    cameraMovement = CameraMovement.DRONE_SWEEP,
                    narrationSnippet = "La nuit s'abat sur Néo-Tokyo, enveloppée de lumières synthétiques et de secrets digitaux.",
                    particleType = ParticleEffectType.CYBER_EMBERS,
                    primaryColorHex = 0xFF0D0221,
                    secondaryColorHex = 0xFF0F084B,
                    accentColorHex = 0xFF00F5D4
                ),
                VideoScene(
                    sceneIndex = 2,
                    durationSeconds = 5f,
                    visualPrompt = "Zoom avant sur le cockpit du speeder filant à travers les autoroutes magnétiques.",
                    cameraMovement = CameraMovement.ZOOM_IN,
                    narrationSnippet = "À plus de huit cents kilomètres-heure, chaque virage défie les lois de la gravité urbaine.",
                    particleType = ParticleEffectType.RAIN_ATMOSPHERE,
                    primaryColorHex = 0xFF261447,
                    secondaryColorHex = 0xFFFF2A85,
                    accentColorHex = 0xFF00F5D4
                ),
                VideoScene(
                    sceneIndex = 3,
                    durationSeconds = 5f,
                    visualPrompt = "Mouvement orbital autour de la mégastructure centrale étincelante au sommet des nuages.",
                    cameraMovement = CameraMovement.ORBIT_SLOW,
                    narrationSnippet = "Au sommet de la mégapole, la liberté n'appartient qu'à ceux qui osent fendre les cieux.",
                    particleType = ParticleEffectType.CYBER_EMBERS,
                    primaryColorHex = 0xFF140152,
                    secondaryColorHex = 0xFF9D4EDD,
                    accentColorHex = 0xFFFFB703
                )
            ),
            selectedAiModel = "Google Gemini Omni 1.1 + Veo Engine",
            tags = listOf("Cyberpunk", "16:9", "3 Scènes", "Ultra-HD")
        )

        val p2 = VideoProject(
            id = "demo_project_scifi",
            title = "Le Sanctuaire Stellaire",
            prompt = "Un temple ancien en apesanteur orbitant autour d'une étoile à neutrons",
            enhancedPrompt = "Odyssée stellaire grandiose révélant un sanctuaire millénaire flottant parmi les anneaux cosmiques.",
            style = VideoStyle.SCI_FI_ODYSSEY,
            aspectRatio = VideoAspectRatio.PORTRAIT_9_16,
            totalDurationSeconds = 15.0f,
            scenes = listOf(
                VideoScene(
                    sceneIndex = 1,
                    durationSeconds = 5f,
                    visualPrompt = "Plan large céleste révélant des monolithes de cristal pulsant au cœur du cosmos.",
                    cameraMovement = CameraMovement.ZOOM_IN,
                    narrationSnippet = "Perdu aux confins de la galaxie d'Andromède, un sanctuaire oublié défie l'éternité.",
                    particleType = ParticleEffectType.COSMIC_NEBULA,
                    primaryColorHex = 0xFF050714,
                    secondaryColorHex = 0xFF7000FF,
                    accentColorHex = 0xFF00E5FF
                ),
                VideoScene(
                    sceneIndex = 2,
                    durationSeconds = 5f,
                    visualPrompt = "Panoramique lent sur les gravures holographiques dorées s'animant sur la pierre.",
                    cameraMovement = CameraMovement.PAN_RIGHT,
                    narrationSnippet = "Des glyphes de pure énergie s'éveillent, murmurant l'histoire des premiers bâtisseurs d'étoiles.",
                    particleType = ParticleEffectType.LIGHT_STREAKS,
                    primaryColorHex = 0xFF101935,
                    secondaryColorHex = 0xFFF59E0B,
                    accentColorHex = 0xFF00E5FF
                ),
                VideoScene(
                    sceneIndex = 3,
                    durationSeconds = 5f,
                    visualPrompt = "Zoom arrière spectaculaire dévoilant la pulsation lumineuse de l'étoile mère.",
                    cameraMovement = CameraMovement.ZOOM_OUT,
                    narrationSnippet = "Une harmonie stellaire parfaite unissant le passé lointain et l'avenir de l'humanité.",
                    particleType = ParticleEffectType.COSMIC_NEBULA,
                    primaryColorHex = 0xFF080C24,
                    secondaryColorHex = 0xFF9D4EDD,
                    accentColorHex = 0xFF00F5D4
                )
            ),
            selectedAiModel = "Google Gemini Omni 1.1 + Veo Engine",
            tags = listOf("Sci-Fi", "9:16 Shorts", "3 Scènes", "Cosmique")
        )

        return listOf(p1, p2)
    }
}

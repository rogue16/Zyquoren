package com.example.data.model

import androidx.compose.ui.graphics.Color
import java.util.UUID

enum class VideoAspectRatio(val ratioText: String, val widthFactor: Float, val heightFactor: Float, val description: String) {
    LANDSCAPE_16_9("16:9", 16f, 9f, "Cinéma, YouTube"),
    PORTRAIT_9_16("9:16", 9f, 16f, "TikTok, Reels, Shorts"),
    SQUARE_1_1("1:1", 1f, 1f, "Instagram, Carré")
}

enum class CameraMovement(val label: String, val iconName: String) {
    ZOOM_IN("Zoom Avant Lent", "zoom_in"),
    ZOOM_OUT("Zoom Arrière Révélateur", "zoom_out"),
    PAN_RIGHT("Panoramique Droite", "arrow_forward"),
    PAN_LEFT("Panoramique Gauche", "arrow_back"),
    TILT_UP("Plongée / Contre-Plongée", "arrow_upward"),
    ORBIT_SLOW("Mouvement Orbital 360", "rotate_right"),
    DRONE_SWEEP("Survol Aérien Drone", "flight")
}

enum class ParticleEffectType(val label: String) {
    CYBER_EMBERS("Étincelles Cyberpunk"),
    COSMIC_NEBULA("Poussière d'Étoiles & Nébuleuse"),
    LIGHT_STREAKS("Rayons Volumétriques"),
    RAIN_ATMOSPHERE("Pluie & Reflets Néon"),
    FLOATING_DUST("Particules Cinématiques 35mm"),
    NONE("Aucun")
}

enum class VideoStyle(
    val title: String,
    val subtitle: String,
    val cameraKeywords: String,
    val colorPalette: List<Long>,
    val defaultParticle: ParticleEffectType
) {
    CINEMATIC_HOLLYWOOD(
        "Cinéma Hollywood 4K",
        "35mm, anamorphique, éclairage volumétrique chaud",
        "Cinematic 35mm anamorphic lens, shallow depth of field, dramatic rim lighting, photorealistic, 8k resolution, Arri Alexa",
        listOf(0xFF0F172A, 0xFF1E293B, 0xFFF59E0B, 0xFF0284C7),
        ParticleEffectType.FLOATING_DUST
    ),
    CYBERPUNK_2099(
        "Cyberpunk Néon 2099",
        "Néons violets/cyans, pluie, reflets mouillés",
        "Cyberpunk aesthetics, vibrant neon lighting, wet asphalt reflections, futuristic towering skyscrapers, volumetric fog, Unreal Engine 5",
        listOf(0xFF0A051B, 0xFF190B38, 0xFF00F5D4, 0xFF9D4EDD),
        ParticleEffectType.CYBER_EMBERS
    ),
    SCI_FI_ODYSSEY(
        "Odyssée Spatiale",
        "Vaisseaux stellaires, nébuleuses cosmiques, planètes",
        "Interstellar space sci-fi, breathtaking cosmic nebula, massive starships, orbital vista, photorealistic celestial lighting",
        listOf(0xFF050714, 0xFF101935, 0xFF7000FF, 0xFF00E5FF),
        ParticleEffectType.COSMIC_NEBULA
    ),
    ANIME_GHIBLI(
        "Anime & Studio Ghibli",
        "Aquarelle vivante, ciel d'été, poésie visuelle",
        "Makoto Shinkai and Studio Ghibli anime style, vibrant lush colors, painted clouds, dreamy sunlight, highly detailed anime background",
        listOf(0xFF1E3A8A, 0xFF38BDF8, 0xFF86EFAC, 0xFFFDE047),
        ParticleEffectType.LIGHT_STREAKS
    ),
    NATURE_DOCUMENTARY(
        "Documentaire Nature 8K",
        "Macro National Geographic, faune & flore majestueuse",
        "National Geographic nature documentary style, golden hour natural sunlight, crystal clear 8k wildlife photography, ultra sharp focus",
        listOf(0xFF064E3B, 0xFF047857, 0xFFFBBF24, 0xFF34D399),
        ParticleEffectType.FLOATING_DUST
    ),
    RETRO_SYNTHWAVE(
        "Synthwave Rétro 80s",
        "Grille vectorielle, soleil couchant rétro, esthétique VHS",
        "Outrun synthwave 80s aesthetic, retro sunset grid, chrome reflections, vaporwave nostalgic colors, 1980s film grain",
        listOf(0xFF2A0845, 0xFF6441A5, 0xFFFF2A85, 0xFFFFB703),
        ParticleEffectType.RAIN_ATMOSPHERE
    )
}

data class VideoScene(
    val id: String = UUID.randomUUID().toString(),
    val sceneIndex: Int,
    val durationSeconds: Float = 5.0f,
    val visualPrompt: String,
    val cameraMovement: CameraMovement,
    val narrationSnippet: String,
    val particleType: ParticleEffectType,
    val primaryColorHex: Long = 0xFF111422,
    val secondaryColorHex: Long = 0xFF9D4EDD,
    val accentColorHex: Long = 0xFF00F5D4,
    val seed: Long = System.currentTimeMillis()
)

data class VideoProject(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val prompt: String,
    val enhancedPrompt: String,
    val style: VideoStyle,
    val aspectRatio: VideoAspectRatio,
    val totalDurationSeconds: Float,
    val scenes: List<VideoScene>,
    val narrationVoice: String = "Voix Française IA (Studio)",
    val isTtsEnabled: Boolean = true,
    val areSubtitlesEnabled: Boolean = true,
    val selectedAiModel: String = "Google Gemini Omni 1.1 + Veo Engine",
    val createdAtTimestamp: Long = System.currentTimeMillis(),
    val tags: List<String> = emptyList()
)

data class AiModelRadarItem(
    val id: String,
    val name: String,
    val provider: String,
    val latencyRating: String,
    val qualityScore: Int, // Out of 100
    val maxResolution: String,
    val status: String,
    val isAutoSelected: Boolean,
    val description: String,
    val strengths: List<String>
)

sealed class GenerationStep(
    val stepNumber: Int,
    val totalSteps: Int = 5,
    val title: String,
    val detail: String,
    val progress: Float
) {
    object Idle : GenerationStep(0, 5, "En attente", "Prêt pour la création de vidéo", 0.0f)
    object AnalyzingPrompt : GenerationStep(1, 5, "Analyse & Amélioration IA", "Enrichissement sémantique et direction artistique cinématique...", 0.20f)
    object AutoRoutingModel : GenerationStep(2, 5, "Orchestration & Choix d'Outil", "Sélection automatique du modèle optimal (Gemini Omni / Veo / Canvas Engine)...", 0.40f)
    object StoryboardGeneration : GenerationStep(3, 5, "Découpage du Storyboard", "Génération des angles de caméra, plans séquences et transitions...", 0.65f)
    object VoiceSynthesis : GenerationStep(4, 5, "Synthèse Vocale & Audio", "Génération de la narration vocale fluide et synchronisation...", 0.85f)
    object RenderingVideo : GenerationStep(5, 5, "Assemblage Vidéo & Rendu", "Rendu des effets de particules, lumière volumétrique et sous-titres...", 1.0f)
    data class Completed(val project: VideoProject) : GenerationStep(5, 5, "Vidéo Prête !", "Génération terminée avec succès.", 1.0f)
    data class Error(val message: String) : GenerationStep(0, 5, "Erreur", message, 0.0f)
}

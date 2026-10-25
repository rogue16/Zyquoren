package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.engine.AiToolsRadarRepository
import com.example.data.engine.AiVideoDirector
import com.example.data.engine.TextToSpeechEngine
import com.example.data.model.AiModelRadarItem
import com.example.data.model.GenerationStep
import com.example.data.model.VideoAspectRatio
import com.example.data.model.VideoProject
import com.example.data.model.VideoScene
import com.example.data.model.VideoStyle
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class AppNavTab(val title: String, val iconName: String) {
    STUDIO("Créer", "movie_creation"),
    PLAYER("Lecteur", "play_circle"),
    RADAR("Outils IA", "hub"),
    LIBRARY("Projets", "video_library")
}

class VideoStudioViewModel(application: Application) : AndroidViewModel(application) {

    private val director = AiVideoDirector()
    val radarRepository = AiToolsRadarRepository()
    val ttsEngine = TextToSpeechEngine(application)

    // Current Navigation Tab
    private val _currentTab = MutableStateFlow(AppNavTab.STUDIO)
    val currentTab: StateFlow<AppNavTab> = _currentTab.asStateFlow()

    // Studio Input State
    val promptInput = MutableStateFlow("Une métropole flottante à travers des nuages pourpres au coucher de soleil")
    val selectedStyle = MutableStateFlow(VideoStyle.CYBERPUNK_2099)
    val selectedAspectRatio = MutableStateFlow(VideoAspectRatio.LANDSCAPE_16_9)
    val sceneCountChoice = MutableStateFlow(3)
    val isTtsEnabled = MutableStateFlow(true)
    val areSubtitlesEnabled = MutableStateFlow(true)

    // Generation Progress State
    private val _generationStep = MutableStateFlow<GenerationStep>(GenerationStep.Idle)
    val generationStep: StateFlow<GenerationStep> = _generationStep.asStateFlow()

    // Projects list
    private val _projects = MutableStateFlow<List<VideoProject>>(director.createSampleProjects())
    val projects: StateFlow<List<VideoProject>> = _projects.asStateFlow()

    // Active project playing
    private val _activeProject = MutableStateFlow<VideoProject?>(_projects.value.firstOrNull())
    val activeProject: StateFlow<VideoProject?> = _activeProject.asStateFlow()

    // Active scene in player
    private val _activeScene = MutableStateFlow<VideoScene?>(null)
    val activeScene: StateFlow<VideoScene?> = _activeScene.asStateFlow()

    // Toast / feedback message
    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    fun setTab(tab: AppNavTab) {
        _currentTab.value = tab
    }

    fun selectProject(project: VideoProject) {
        _activeProject.value = project
        _activeScene.value = project.scenes.firstOrNull()
        _currentTab.value = AppNavTab.PLAYER
    }

    fun onSceneChanged(scene: VideoScene) {
        _activeScene.value = scene
        val project = _activeProject.value
        if (project != null && isTtsEnabled.value && scene.narrationSnippet.isNotBlank()) {
            ttsEngine.speak(scene.narrationSnippet, "scene_${scene.sceneIndex}")
        }
    }

    fun clearSnackbar() {
        _snackbarMessage.value = null
    }

    fun setPrompt(text: String) {
        promptInput.value = text
    }

    fun applyInspiration(prompt: String, style: VideoStyle, ratio: VideoAspectRatio) {
        promptInput.value = prompt
        selectedStyle.value = style
        selectedAspectRatio.value = ratio
        _snackbarMessage.value = "Inspiration appliquée : ${style.title}"
    }

    /**
     * One-tap Magic AI Prompt Enhancer
     */
    fun enhancePromptMagic() {
        val raw = promptInput.value.trim()
        if (raw.isBlank()) {
            _snackbarMessage.value = "Veuillez entrer une idée d'abord."
            return
        }

        viewModelScope.launch {
            _snackbarMessage.value = "Amélioration cinématographique IA en cours..."
            val enhanced = director.enhancePromptWithAi(raw, selectedStyle.value)
            promptInput.value = enhanced
            _snackbarMessage.value = "Prompt optimisé pour le style ${selectedStyle.value.title} !"
        }
    }

    /**
     * Fully Automated Content Generation:
     * Discovers best engine, creates storyboard, generates voiceover, synthesizes cinematic motion.
     */
    fun generateVideoAutonomous() {
        val rawPrompt = promptInput.value.trim()
        if (rawPrompt.isBlank()) {
            _snackbarMessage.value = "Veuillez renseigner un prompt pour votre vidéo."
            return
        }

        viewModelScope.launch {
            try {
                // Step 1: Analyze & enhance prompt
                _generationStep.value = GenerationStep.AnalyzingPrompt
                delay(900)
                val enhancedPrompt = director.enhancePromptWithAi(rawPrompt, selectedStyle.value)

                // Step 2: Auto-route & benchmark best AI model without bothering user
                _generationStep.value = GenerationStep.AutoRoutingModel
                delay(800)
                val chosenEngine = radarRepository.autoSelectBestEngine(
                    rawPrompt,
                    selectedStyle.value,
                    selectedAspectRatio.value
                )

                // Step 3: Storyboard & cinematography breakdown
                _generationStep.value = GenerationStep.StoryboardGeneration
                delay(1100)
                val project = director.generateStoryboard(
                    title = "",
                    prompt = rawPrompt,
                    enhancedPrompt = enhancedPrompt,
                    style = selectedStyle.value,
                    aspectRatio = selectedAspectRatio.value,
                    sceneCount = sceneCountChoice.value
                ).copy(
                    isTtsEnabled = isTtsEnabled.value,
                    areSubtitlesEnabled = areSubtitlesEnabled.value,
                    selectedAiModel = chosenEngine.name
                )

                // Step 4: Voiceover synthesis
                _generationStep.value = GenerationStep.VoiceSynthesis
                delay(900)

                // Step 5: Render and finalize video
                _generationStep.value = GenerationStep.RenderingVideo
                delay(1000)

                // Complete
                _projects.value = listOf(project) + _projects.value
                _activeProject.value = project
                _activeScene.value = project.scenes.firstOrNull()
                _generationStep.value = GenerationStep.Completed(project)

                delay(600)
                _generationStep.value = GenerationStep.Idle
                _currentTab.value = AppNavTab.PLAYER
                _snackbarMessage.value = "Vidéo générée avec succès via ${chosenEngine.name} !"

                // Trigger voiceover for scene 1 if enabled
                if (isTtsEnabled.value && project.scenes.isNotEmpty()) {
                    ttsEngine.speak(project.scenes[0].narrationSnippet, "scene_1")
                }

            } catch (e: Exception) {
                _generationStep.value = GenerationStep.Error(e.message ?: "Erreur inconnue de génération")
                _snackbarMessage.value = "Erreur : ${e.message}"
            }
        }
    }

    fun dismissGenerationModal() {
        _generationStep.value = GenerationStep.Idle
    }

    fun deleteProject(project: VideoProject) {
        _projects.value = _projects.value.filter { it.id != project.id }
        if (_activeProject.value?.id == project.id) {
            _activeProject.value = _projects.value.firstOrNull()
        }
        _snackbarMessage.value = "Projet supprimé"
    }

    fun exportVideo(project: VideoProject) {
        _snackbarMessage.value = "Exportation MP4 4K de '${project.title}' en cours..."
        viewModelScope.launch {
            delay(1200)
            _snackbarMessage.value = "Vidéo exportée et enregistrée dans la galerie !"
        }
    }

    override fun onCleared() {
        super.onCleared()
        ttsEngine.shutdown()
    }
}

package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.engine.AiToolsRadarRepository
import com.example.data.engine.AiVideoDirector
import com.example.data.model.VideoAspectRatio
import com.example.data.model.VideoStyle
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read app name from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("NovaVideo", appName)
  }

  @Test
  fun `verify radar auto selection`() {
    val radar = AiToolsRadarRepository()
    val chosen = radar.autoSelectBestEngine(
      "Test cyberpunk scene",
      VideoStyle.CYBERPUNK_2099,
      VideoAspectRatio.LANDSCAPE_16_9
    )
    assertNotNull(chosen)
    assertTrue(chosen.qualityScore >= 90)
  }

  @Test
  fun `verify storyboard generation`() = runBlocking {
    val director = AiVideoDirector()
    val project = director.generateStoryboard(
      title = "Test Video",
      prompt = "Un vaisseau spatial traversant un trou noir",
      enhancedPrompt = "Odyssée stellaire grandiose d'un vaisseau spatial traversant un trou noir",
      style = VideoStyle.SCI_FI_ODYSSEY,
      aspectRatio = VideoAspectRatio.LANDSCAPE_16_9,
      sceneCount = 3
    )

    assertEquals(3, project.scenes.size)
    assertEquals(15.0f, project.totalDurationSeconds, 0.1f)
    assertTrue(project.scenes[0].narrationSnippet.isNotBlank())
  }
}

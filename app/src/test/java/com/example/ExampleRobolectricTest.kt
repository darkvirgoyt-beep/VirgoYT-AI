package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.virgoyt.data.model.OutputType
import com.example.virgoyt.data.terminal.TerminalEngine
import com.example.virgoyt.data.vfs.VirtualFileSystem
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("VirgoYT Cloud AI", appName)
    }

    @Test
    fun `virtual file system operations`() {
        val vfs = VirtualFileSystem()
        val pkgFile = vfs.getFile("/workspace/package.json")
        assertNotNull(pkgFile)
        assertTrue(pkgFile!!.content.contains("virgoyt-cloud-application"))

        // Create and read a new file
        vfs.writeFile("/workspace/test.txt", "Hello from VirgoYT Cloud VFS")
        val content = vfs.readFile("/workspace/test.txt")
        assertEquals("Hello from VirgoYT Cloud VFS", content)

        // Delete file
        val deleted = vfs.deleteFile("/workspace/test.txt")
        assertTrue(deleted)
    }

    @Test
    fun `terminal engine command execution`() = runBlocking {
        val vfs = VirtualFileSystem()
        val terminal = TerminalEngine(vfs)

        terminal.executeCommand("pwd")
        val entries = terminal.terminalEntries.value
        assertTrue(entries.any { it.text.contains("/workspace") })

        terminal.executeCommand("test")
        val testEntries = terminal.terminalEntries.value
        assertTrue(testEntries.any { it.text.contains("passed") })
    }

    @Test
    fun `conversational engine reasoning and hard thinking`() = runBlocking {
        val vfs = VirtualFileSystem()
        val engine = com.example.virgoyt.data.agent.VirgoConversationalEngine(vfs)

        // Test user greeting
        val greeting = engine.generateAgentReply("Hi", com.example.virgoyt.data.model.AiModelTier.AUTO_ROUTER)
        assertNotNull(greeting.content)
        assertTrue(greeting.content.contains("VirgoYT"))
        assertTrue(greeting.followUpQuestions.isNotEmpty())

        // Test coding request generates reasoning, code, and files
        val codeReply = engine.generateAgentReply("write python script to process data", com.example.virgoyt.data.model.AiModelTier.GEMINI_2_5_FLASH)
        assertNotNull(codeReply.reasoningThought)
        assertTrue(codeReply.reasoningThought!!.contains("Problem Decomposition"))
        assertTrue(codeReply.codeSnippets.isNotEmpty())
        assertTrue(codeReply.generatedFiles.isNotEmpty())
        assertTrue(codeReply.terminalCommands.isNotEmpty())

        // Test media generation generates artifacts
        val mediaReply = engine.generateAgentReply("generate pic of cyberpunk city", com.example.virgoyt.data.model.AiModelTier.GEMINI_2_5_FLASH)
        assertTrue(mediaReply.mediaGenerations.isNotEmpty())
        assertEquals("image", mediaReply.mediaGenerations[0].type)
        assertNotNull(mediaReply.reasoningThought)

        // Test custom API key configuration
        engine.setCustomApiKey("AIzaSyFakeTestKey123456789")
        assertTrue(engine.hasValidGeminiKey())

        // Test Termux & Claude Code harness intent
        val termuxReply = engine.generateAgentReply("how to run in termux like claude code harness", com.example.virgoyt.data.model.AiModelTier.AUTO_ROUTER)
        assertTrue(termuxReply.content.contains("Directly in Termux"))
        assertTrue(termuxReply.content.contains("openjdk-17"))
        assertTrue(termuxReply.terminalCommands.any { it.contains("pkg install") })
    }
}


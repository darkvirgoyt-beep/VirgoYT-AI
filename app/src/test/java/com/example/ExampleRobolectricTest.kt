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
}


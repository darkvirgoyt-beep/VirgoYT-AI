package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.manus.data.model.OutputType
import com.example.manus.data.terminal.TerminalEngine
import com.example.manus.data.vfs.VirtualFileSystem
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
        val indexFile = vfs.getFile("/workspace/index.html")
        assertNotNull(indexFile)
        assertTrue(indexFile!!.content.contains("<!DOCTYPE html>"))

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

        // Test pwd
        val pwdOut = terminal.executeCommand("pwd")
        assertEquals("/workspace", pwdOut)

        // Test echo
        val echoOut = terminal.executeCommand("echo 'VirgoYT Cloud AI'")
        assertEquals("VirgoYT Cloud AI", echoOut)

        // Test python execution
        val pyOut = terminal.executeCommand("python3 scripts/data_analyzer.py")
        assertTrue(pyOut.contains("Regression Model Fitted"))

        // Test node execution
        val nodeOut = terminal.executeCommand("node scripts/benchmark.js")
        assertTrue(nodeOut.contains("Benchmark Score"))

        // Test C compilation
        val cOut = terminal.executeCommand("gcc main.c -o sort && ./sort")
        assertTrue(cOut.contains("Compilation successful") || terminal.entries.value.any { it.text.contains("Compilation successful") })
    }
}

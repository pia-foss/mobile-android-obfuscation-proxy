/*
 * Copyright (c) "2023" Private Internet Access, Inc.
 *
 *  This file is part of the Private Internet Access Android Client.
 *
 *  The Private Internet Access Android Client is free software: you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License as published by the Free
 *  Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *  The Private Internet Access Android Client is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 *  or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 *  details.
 *
 *  You should have received a copy of the GNU General Public License along with the Private
 *  Internet Access Android Client.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package com.kape.obfuscator.data.externals

import android.content.Context
import android.os.Build
import java.io.File
import java.util.zip.ZipFile

internal class FilePathImpl(
    private val context: Context
) : FilePath {

    companion object {
        private const val EXECUTABLE_LIBRARY_NAME = "libsslocal.so"
    }

    // region FilePath
    override fun getExecutablePath(): Result<String> {
        val nativeLibFile = File(context.applicationInfo.nativeLibraryDir, EXECUTABLE_LIBRARY_NAME)
        if (nativeLibFile.exists()) {
            return Result.success(nativeLibFile.canonicalFile.absolutePath)
        }
        // extractNativeLibs=false: the .so was not extracted to the filesystem.
        // Fall back to copying it out of the APK so it can be executed as a subprocess.
        return extractFromApk()
    }
    // endregion

    private fun extractFromApk(): Result<String> {
        val abi = Build.SUPPORTED_ABIS.firstOrNull()
            ?: return Result.failure(Exception("No supported ABI found."))
        val entryName = "lib/$abi/$EXECUTABLE_LIBRARY_NAME"

        val apkPaths = mutableListOf(context.applicationInfo.sourceDir)
        context.applicationInfo.splitSourceDirs?.let { apkPaths.addAll(it) }

        val targetFile = File(context.filesDir, EXECUTABLE_LIBRARY_NAME)
        val apkLastModified = File(context.applicationInfo.sourceDir).lastModified()
        if (targetFile.exists() && targetFile.lastModified() >= apkLastModified) {
            return Result.success(targetFile.absolutePath)
        }

        for (apkPath in apkPaths) {
            try {
                ZipFile(apkPath).use { zip ->
                    val entry = zip.getEntry(entryName) ?: return@use
                    zip.getInputStream(entry).use { input ->
                        targetFile.outputStream().use { output ->
                            input.copyTo(output)
                        }
                    }
                }
                if (targetFile.exists()) {
                    targetFile.setExecutable(true)
                    return Result.success(targetFile.absolutePath)
                }
            } catch (e: Exception) {
                continue
            }
        }

        return Result.failure(Exception("$EXECUTABLE_LIBRARY_NAME not found in APK for ABI $abi."))
    }
}

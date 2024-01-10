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

import com.kape.obfuscator.presenter.ObfuscatorError
import com.kape.obfuscator.presenter.ObfuscatorErrorCode
import java.lang.Error

internal class ShadowsocksProcessImpl(
    private val filePath: FilePath
) : ShadowsocksProcess {

    // region ShadowsocksProcess
    override fun start(commandLineParams: List<String>): Result<Process> {
        val executablePath = filePath.getExecutablePath().getOrElse {
            return Result.failure(
                ObfuscatorError(
                    code = ObfuscatorErrorCode.PROCESS_COULD_NOT_START,
                    error = Error("Executable path missing.")
                )
            )
        }

        val commands = commandLineParams.toMutableList()
        commands.add(0, executablePath)

        val processBuilder = ProcessBuilder(commands)
        val environment: MutableMap<String, String> = processBuilder.environment()
        environment.clear()
        processBuilder.redirectErrorStream(true)

        return try {
            val process: Process = processBuilder.start().apply {
                // We have nothing to input to the process's output. Close it.
                outputStream.close()
            }
            Result.success(process)
        } catch (throwable: Throwable) {
            Result.failure(
                ObfuscatorError(
                    code = ObfuscatorErrorCode.PROCESS_COULD_NOT_START,
                    error = Error(throwable.message)
                )
            )
        }
    }

    override fun stop(pid: Int): Result<Unit> {
        try {
            android.os.Process.killProcess(pid)
            return Result.success(Unit)
        } catch (throwable: Throwable) {
            return Result.failure(
                ObfuscatorError(
                    code = ObfuscatorErrorCode.PROCESS_COULD_NOT_STOP,
                    error = Error(throwable.message)
                )
            )
        }
    }
    // endregion
}

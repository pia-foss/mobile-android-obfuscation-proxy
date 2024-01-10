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

package com.kape.obfuscator.domain.controllers

import com.kape.obfuscator.data.utils.getOrFail
import com.kape.obfuscator.domain.usecases.ClearCache
import com.kape.obfuscator.domain.usecases.CreateProcessListeningOnDeferrable
import com.kape.obfuscator.domain.usecases.IsProcessStopped
import com.kape.obfuscator.domain.usecases.ProcessOutputHandler
import com.kape.obfuscator.domain.usecases.StartProcess
import com.kape.obfuscator.domain.usecases.StartProcessOutputHandler
import com.kape.obfuscator.domain.usecases.StartProcessOutputReader
import com.kape.obfuscator.domain.usecases.WaitForProcessListeningOnDeferrable
import com.kape.obfuscator.presenter.ObfuscatorProcessEventHandler

internal class StartProcessControllerImpl(
    private val isProcessStopped: IsProcessStopped,
    private val createProcessListeningOnDeferrable: CreateProcessListeningOnDeferrable,
    private val processOutputHandler: ProcessOutputHandler,
    private val startProcessOutputHandler: StartProcessOutputHandler,
    private val startProcess: StartProcess,
    private val startProcessOutputReader: StartProcessOutputReader,
    private val waitForProcessListeningOnDeferrable: WaitForProcessListeningOnDeferrable,
    private val clearCache: ClearCache
) : StartProcessController {

    // region StartProcessController
    override suspend fun invoke(
        commandLineParams: List<String>,
        obfuscatorProcessEventHandler: ObfuscatorProcessEventHandler
    ): Result<Unit> {
        val onFailure: suspend (throwable: Throwable) -> Unit = {
            clearCache()
            throw it
        }

        return isProcessStopped()
            .mapCatching {
                createProcessListeningOnDeferrable().getOrFail(onFailure = onFailure)
            }
            .mapCatching {
                startProcessOutputHandler(
                    obfuscatorProcessEventHandler = obfuscatorProcessEventHandler
                ).getOrFail(onFailure = onFailure)
            }
            .mapCatching {
                startProcess(commandLineParams = commandLineParams).getOrFail(onFailure = onFailure)
            }
            .mapCatching {
                startProcessOutputReader(
                    processOutputHandler = processOutputHandler
                ).getOrFail(onFailure = onFailure)
            }
            .mapCatching {
                waitForProcessListeningOnDeferrable().getOrFail(onFailure = onFailure)
            }
    }
    // endregion
}

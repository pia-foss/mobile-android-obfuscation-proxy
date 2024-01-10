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

package com.kape.obfuscator.domain.usecases

import android.util.Log
import com.kape.obfuscator.presenter.ObfuscatorProcessEventHandler

internal class StartProcessOutputHandlerImpl(
    private val handleProcessErrorOutput: HandleProcessErrorOutput,
    private val handleProcessSuccessOutput: HandleProcessSuccessOutput
) : StartProcessOutputHandler, ProcessOutputHandler {

    private lateinit var obfuscatorProcessEventHandler: ObfuscatorProcessEventHandler

    companion object {
        private const val SHADOWSOCKS_TAG = "Shadowsocks/Process"
    }

    private enum class ShadowsocksProcessOutput(val command: String) {
        ERROR_OUTPUT("error"),
        SUCCESS_OUTPUT("listening on")
    }

    // region StartProcessOutputHandler
    override suspend fun invoke(
        obfuscatorProcessEventHandler: ObfuscatorProcessEventHandler
    ): Result<Unit> {
        this.obfuscatorProcessEventHandler = obfuscatorProcessEventHandler
        return Result.success(Unit)
    }
    // endregion

    // region ProcessOutputHandler
    override fun output(line: String) {
        Log.d(SHADOWSOCKS_TAG, line)
        val sanitizedString = line.lowercase()
        when {
            sanitizedString.contains(ShadowsocksProcessOutput.ERROR_OUTPUT.command) ->
                handleProcessErrorOutput(obfuscatorProcessEventHandler = obfuscatorProcessEventHandler)
            sanitizedString.contains(ShadowsocksProcessOutput.SUCCESS_OUTPUT.command) ->
                handleProcessSuccessOutput()
        }
    }
    // endregion
}

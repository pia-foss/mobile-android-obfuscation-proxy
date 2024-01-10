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

import com.kape.obfuscator.data.utils.pid
import com.kape.obfuscator.presenter.ObfuscatorError
import com.kape.obfuscator.presenter.ObfuscatorErrorCode
import kotlinx.coroutines.CompletableDeferred

internal class CacheImpl : Cache {

    private var process: Process? = null
    private var processListeningOnCompletableDeferred: CompletableDeferred<Unit>? = null

    // region Cache
    override fun clear(): Result<Unit> {
        return clearProcess()
            .mapCatching { clearProcessListeningOnDeferrable().getOrThrow() }
    }

    override fun setProcess(process: Process): Result<Unit> {
        this.process = process
        return Result.success(Unit)
    }

    override fun getProcess(): Result<Process> =
        runCatching {
            process ?: throw ObfuscatorError(code = ObfuscatorErrorCode.PROCESS_UNKNOWN)
        }

    override fun getProcessId(): Result<Int> =
        getProcess().mapCatching {
            it.pid().getOrThrow()
        }

    private fun clearProcess(): Result<Unit> {
        process = null
        return Result.success(Unit)
    }

    override fun createProcessListeningOnDeferrable(): Result<Unit> {
        this.processListeningOnCompletableDeferred = CompletableDeferred()
        return Result.success(Unit)
    }

    override fun getProcessListeningOnDeferrable(): Result<CompletableDeferred<Unit>> =
        processListeningOnCompletableDeferred?.let {
            Result.success(it)
        } ?: Result.failure(
            ObfuscatorError(code = ObfuscatorErrorCode.PROCESS_DEFERRABLE_NOT_READY)
        )

    private fun clearProcessListeningOnDeferrable(): Result<Unit> {
        processListeningOnCompletableDeferred = null
        return Result.success(Unit)
    }
    // endregion
}

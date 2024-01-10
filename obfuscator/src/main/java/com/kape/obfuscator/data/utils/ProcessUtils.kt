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

package com.kape.obfuscator.data.utils

import com.kape.obfuscator.presenter.ObfuscatorError
import com.kape.obfuscator.presenter.ObfuscatorErrorCode
import java.io.File
import java.lang.Error

internal class ProcessUtils {

    companion object {
        internal const val PROCESS_IDENTIFIER = "pid"
    }
}

internal fun Process.pid(): Result<Int> {
    return try {
        val pidField = this.javaClass.getDeclaredField(ProcessUtils.PROCESS_IDENTIFIER)
        pidField.isAccessible = true
        val pid = pidField.getInt(this)
        pidField.isAccessible = false
        Result.success(pid)
    } catch (throwable: Throwable) {
        Result.failure(
            ObfuscatorError(
                code = ObfuscatorErrorCode.INVALID_PID,
                error = Error(throwable.message)
            )
        )
    }
}

internal fun Process.isRunning(): Result<Unit> {
    return this.pid().mapCatching {
        if (File("/proc/$it").exists()) {
            Result.success(Unit)
        } else {
            Result.failure(
                ObfuscatorError(
                    code = ObfuscatorErrorCode.PROCESS_NOT_RUNNING
                )
            )
        }
    }
}

/*
 * Copyright (c) "2024" Private Internet Access, Inc.
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

import com.kape.obfuscator.data.externals.Cache
import com.kape.obfuscator.presenter.ObfuscatorError
import com.kape.obfuscator.presenter.ObfuscatorErrorCode
import kotlinx.coroutines.withTimeoutOrNull

internal class WaitForProcessListeningOnDeferrableImpl(
    private val cache: Cache
) : WaitForProcessListeningOnDeferrable {

    companion object {
        private const val PROCESS_LISTENING_ON_TIMEOUT_MS = 3000L
    }

    // region WaitForProcessListeningOnDeferrable
    override suspend fun invoke(): Result<Unit> {
        val deferrable = cache.getProcessListeningOnDeferrable().getOrElse {
            return Result.failure(it)
        }

        return withTimeoutOrNull(timeMillis = PROCESS_LISTENING_ON_TIMEOUT_MS) {
            deferrable.await()
        }?.let {
            Result.success(Unit)
        } ?: Result.failure(
            ObfuscatorError(code = ObfuscatorErrorCode.PROCESS_DEFERRABLE_TIMED_OUT)
        )
    }
    // endregion
}

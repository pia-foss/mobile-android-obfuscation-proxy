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

import com.kape.obfuscator.domain.usecases.ClearCache
import com.kape.obfuscator.domain.usecases.IsProcessRunning
import com.kape.obfuscator.domain.usecases.StopProcess

internal class StopProcessControllerImpl(
    private val isProcessRunning: IsProcessRunning,
    private val stopProcess: StopProcess,
    private val clearCache: ClearCache
) : StopProcessController {

    // region StopProcessController
    override suspend fun invoke(): Result<Unit> =
        isProcessRunning()
            .mapCatching {
                stopProcess().getOrThrow()
            }
            .mapCatching {
                clearCache().getOrThrow()
            }
    // endregion
}

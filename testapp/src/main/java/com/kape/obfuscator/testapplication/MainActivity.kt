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

package com.kape.obfuscator.testapplication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kape.obfuscator.testapp.databinding.ActivityMainBinding
import com.kape.obfuscator.presenter.ObfuscatorAPI
import com.kape.obfuscator.presenter.ObfuscatorBuilder
import com.kape.obfuscator.presenter.ObfuscatorProcessEventHandler
import kotlinx.coroutines.Dispatchers

class MainActivity : AppCompatActivity(), ObfuscatorProcessEventHandler {

    private lateinit var binding: ActivityMainBinding
    private lateinit var uiLogger: UILogger

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        uiLogger = UILogger(textViewOutput = binding.textviewOutput)

        val obfuscatorAPI: ObfuscatorAPI = ObfuscatorBuilder()
            .setContext(this.applicationContext)
            .setClientCoroutineContext(Dispatchers.Main)
            .build()

        binding.connectButton.setOnClickListener {
            obfuscatorAPI.start(
                commandLineParams = listOf(
                    "-v", "--log-without-time",
                    "-s", "154.47.20.240:443",
                    "-k", "shadowsocks",
                    "-b", "127.0.0.1:8383",
                    "-m", "aes-128-gcm",
                ),
                obfuscatorProcessEventHandler = this
            ) {
                uiLogger.log("Start result: $it")
            }
        }

        binding.disconnectButton.setOnClickListener {
            obfuscatorAPI.stop {
                uiLogger.log("Stop result: $it")
            }
        }

        binding.clearLogsButton.setOnClickListener {
            binding.textviewOutput.text = ""
        }
    }

    // region ShadowsocksProcessEventHandler
    override fun processStopped(): Result<Unit> {
        uiLogger.log("Process Stopped")
        return Result.success(Unit)
    }
    // endregion
}
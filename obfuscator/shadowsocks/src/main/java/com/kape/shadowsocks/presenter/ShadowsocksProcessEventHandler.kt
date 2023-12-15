package com.kape.shadowsocks.presenter

/**
 * Interface defining the handler of shadowsocks's process relevant events.
 */
public interface ShadowsocksProcessEventHandler {

    /**
     * @return `Result<Unit>`
     */
    fun processStopped(): Result<Unit>
}

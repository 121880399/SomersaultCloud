package org.somersault.cloud.lib.utils

import java.io.Serializable

/**
 * ================================================
 * 作    者：ZhouZhengyi
 * 创建日期：2022/7/17 18:59
 * 描    述：存储指定时间范围的节点，超过该范围，最早的时间节点会被移除
 * 修订历史：
 * ================================================
 */
class TimeLruCache<V> : Serializable{

    private val linkedHashMap : LinkedHashMap<Long, V> = TimeLinkedHashMap(0, 0.75f, false)

    /**
     * 默认为30s
     * 作者:ZhouZhengyi
     * 创建时间: 2022/7/17 19:13
     */
    private var periodOfTime = 30L * 1000

    /**
     * 上一次put数据的时间
     * 作者:ZhouZhengyi
     * 创建时间: 2022/7/17 19:15
     */
    private var lastPutTime = 0L

    /**
     * 上一次Put的数据
     * 作者:ZhouZhengyi
     * 创建时间: 2022/7/17 19:16
     */
    private var lastPutValue : V? = null

    constructor(){}

    /**
     * 当最后一个节点和第一个节点的时间差超过periodOfTime时，
     * 第一个节点会从LinkedHashMap中移除
     * 作者:ZhouZhengyi
     * 创建时间: 2022/7/17 19:13
     */
    constructor(periodOfTime : Long){
        this.periodOfTime = periodOfTime
    }

    /**
     * 插入数据
     * 作者:ZhouZhengyi
     * 创建时间: 2022/7/17 19:16
     */
    public fun put(time : Long, value : V){
        linkedHashMap[time] = value
        lastPutTime = time
        lastPutValue = value
    }

    private class TimeLinkedHashMap<V> : LinkedHashMap<Long, V>{

        constructor(initCapacity: Int, loadFactor: Float, accessOrder: Boolean) : super(initCapacity, loadFactor, accessOrder)

        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<Long, V>?): Boolean {
            return size > 10
        }
    }
}
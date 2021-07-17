package org.zzy.somersault.view_click;

import java.util.List;

/**
 * ================================================
 * 作    者：ZhouZhengyi
 * 创建日期：2021/7/13 8:29
 * 描    述：用来描述需要被Hook的方法
 * 修订历史：
 * ================================================
 */
public class MethodBean {

    /**
     * 原方法名
     */
    public String name;
    /**
     * 原方法描述
     */
    public String desc;
    /**
     * 方法所在的接口或类
     */
    public String parent;
    /**
     * 代理方法名
     */
    public String agentName;
    /**
     * 采集数据的方法描述
     */
    public String agentDesc;
    /**
     * 代理方法参数起始索引（ 0：this，1+：普通参数 ）
     */
    public int paramsStart;
    /**
     * 代理方法的参数个数
     */
    public int paramsCount;
    /**
     * 参数类型对应的ASM指令，加载不同类型的参数需要不同的指令
     */
    public List<Integer> opcodes;


    MethodBean(String name, String desc, String agentName) {
        this.name = name;
        this.desc = desc;
        this.agentName = agentName;
    }

    MethodBean(String name, String desc, String parent, String agentName, String agentDesc, int paramsStart, int paramsCount, List<Integer> opcodes) {
        this.name = name;
        this.desc = desc;
        this.parent = parent;
        this.agentName = agentName;
        this.agentDesc = agentDesc;
        this.paramsStart = paramsStart;
        this.paramsCount = paramsCount;
        this.opcodes = opcodes;
    }
}

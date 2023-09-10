package smf.lovemin;

import smf.lovemin.JsonUtils.ManifestTransaction;

/**
 * @author SMF & icdada
 * @描述: 绑定输出类
 * <p>
 * 包含程序自定义返回值类型 Result，该类型可绑定两个互关值进行捆绑输出。
 * </p>
 */
public class Result {
    private ManifestTransaction.ManifestType from,to;

    public Result(ManifestTransaction.ManifestType from, ManifestTransaction.ManifestType to){
        this.from = from;
        this.to = to;
    }

    public ManifestTransaction.ManifestType getFrom() {
        return from;
    }

    public ManifestTransaction.ManifestType getTo() {
        return to;
    }
}
package net.ys.constant;

/**
 * User: NMY
 * Date: 17-5-17
 */
public interface X {

    interface TIME {
        //对应秒
        int DAY_SECOND = 24 * 60 * 60;
        int HOUR_SECOND = 60 * 60;
        int MINUTE = 60;
        //对应毫秒
        int DAY_MILLISECOND = 24 * 60 * 60 * 1000;
        int HOUR_MILLISECOND = 60 * 60 * 1000;
        int MINUTE_MILLISECOND = 60 * 1000;
        int SECOND_MILLISECOND = 1000;
    }

    /**
     * 编码
     */
    interface ENCODING {
        String U = "UTF-8";
        String I = "ISO8859-1";
    }

    /**
     * 邮件
     */
    interface EMAIL {
        String HOST = "smtp.qq.com";
        String USERNAME = "529726271@qq.com";
        String PASSWORD = "djvzaksvnarrbgec";
        String SUBJECT = "紧急情况处理";
        String CONTENT = "【数据共享中心】尊敬的管理员【%s】，数据共享中心监控到与【%s】的对接中断，请尽快进行排查！";
    }

    /**
     * 配置
     */
    interface CFG {
        String TABLE_PREFIX = "T_";
    }
}

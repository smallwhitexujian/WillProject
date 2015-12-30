package com.anykey.balala;

/**
 * Created by xujian on 15/8/26.
 * 接口地址配置文件
 */
public class CommonUrlConfig {
    private static boolean isDebug = AppBalala.isDebug;
    public static String TEST_HOST_URL = "http://apitest.gamedonk.com/";
    public static String HOST_URL = "http://api.balala.co.id/"; //
    public static final String OUT_IP = isDebug ? "54.255.187.49" : "dtlogin.balala.co.id";
    public static final int OUT_PORT = isDebug ? 13302 : 13302;

    /**
     * 接口返回状态码
     */
    public interface RequestState {
        String OK = "1000";
        String ERR = "1008";
        String REPEAT = "2002";
        String OUT = "2003";
        String Logout = "1003";
    }

    public static String apkUp = (isDebug ? TEST_HOST_URL : HOST_URL) + "web/ApkUpgrade.aspx";
    //总配置文件url
    public static String XML_HOST_RUL = (isDebug ? TEST_HOST_URL : HOST_URL) + "Upload/baseconfig.xml";
    public static String phoneRegister = (isDebug ? TEST_HOST_URL : HOST_URL) + "webServers/User/UserSmRegister";
    public static String getPhoneCode = (isDebug ? TEST_HOST_URL : HOST_URL) + "webServers/User/GetPhoneCode"; //获取手机验证码
    public static String DynamicLssue = (isDebug ? TEST_HOST_URL : HOST_URL) + "Webservers/Find/DynamicLssue"; //发布动态
    public static String DiscoverySquare = (isDebug ? TEST_HOST_URL : HOST_URL) + "Webservers/Find/DiscoverySquare"; //动态列表
    public static String PersonalDynamics = (isDebug ? TEST_HOST_URL : HOST_URL) + "Webservers/Messages/PersonalDynamics"; //个人动态/他人动态
    public static String UserResetPassword = (isDebug ? TEST_HOST_URL : HOST_URL) + "Webservers/User/UserSmRetrievePwd";//重置密码
    public static String DiscoveryPraise = (isDebug ? TEST_HOST_URL : HOST_URL) + "/Webservers/Find/DiscoveryPraise"; //点赞
    public static String DynamicDel = (isDebug ? TEST_HOST_URL : HOST_URL) + "/Webservers/Find/DynamicDel"; //删除动态
    public static String DiscoveryCommentAdd = (isDebug ? TEST_HOST_URL : HOST_URL) + "Webservers/Find/DiscoveryCommentAdd"; //添加评论
    public static String DiscoveryCommentList = (isDebug ? TEST_HOST_URL : HOST_URL) + "Webservers/Find/DiscoveryCommentList"; // 获取评论
    public static String DiscoveryCommentDel = (isDebug ? TEST_HOST_URL : HOST_URL) + "Webservers/Find/DiscoveryCommentDel"; // 删除评论
    public static String UserInformation = (isDebug ? TEST_HOST_URL : HOST_URL) + "Webservers/Messages/UserInformation"; // 个人信息
    public static String FeedbackAdd = (isDebug ? TEST_HOST_URL : HOST_URL) + "Webservers/User/FeedbackAdd"; // 发布意见反馈
    public static String AreaList = (isDebug ? TEST_HOST_URL : HOST_URL) + "Webservers/Messages/AreaList"; // 地区列表
    public static String FindPeople = (isDebug ? TEST_HOST_URL : HOST_URL) + "Webservers/Find/FindPeople"; // FindPeople
    public static String SendRedPaper = (isDebug ? TEST_HOST_URL : HOST_URL) + "Webservers/bar/SendRedPaper"; //发红包
    public static String RedPaperGetCheck = (isDebug ? TEST_HOST_URL : HOST_URL) + "Webservers/bar/RedPaperGetCheck"; //检查红包
    public static String GetRedPaper = (isDebug ? TEST_HOST_URL : HOST_URL) + "Webservers/bar/GetRedPaper"; //拆开红包
    public static String RedPaperGetlist = (isDebug ? TEST_HOST_URL : HOST_URL) + "Webservers/bar/RedPaperGetlist"; //红包明细
    public static String RedPaperRankList = (isDebug ? TEST_HOST_URL : HOST_URL) + "Webservers/bar/RedPaperRankList"; //我的收发红包明细
    public static String UserInformationEdit = (isDebug ? TEST_HOST_URL : HOST_URL) + "Webservers/Messages/UserInformationEdit"; //修改用户信息
    public static String TaskList = (isDebug ? TEST_HOST_URL : HOST_URL) + "Webservers/Messages/TaskList"; //用户列表
    public static String TaskAccomplishTasks = (isDebug ? TEST_HOST_URL : HOST_URL) + "Webservers/Messages/TaskAccomplishTasks"; //做任务
    public static String TaskGetCoin = (isDebug ? TEST_HOST_URL : HOST_URL) + "Webservers/Messages/TaskGetCoin"; //领币
    public static String PropUserHonor = (isDebug ? TEST_HOST_URL : HOST_URL) + "Webservers/Messages/PropUserHonor"; //我的礼物列表
    public static String HeInformation = (isDebug ? TEST_HOST_URL : HOST_URL) + "Webservers/Messages/HeInformation"; //看别人的资料
    public static String VoiceUpload = (isDebug ? TEST_HOST_URL : HOST_URL) + "Webservers/Voice/VoiceUpload"; //上传语音文件
    public static String DiscoveryFollow = (isDebug ? TEST_HOST_URL : HOST_URL) + "Webservers/find/DiscoveryFollow"; //我关注的动态
    /**
     * 登陆注册接口
     */
    public static String facebookRegister = (isDebug ? TEST_HOST_URL : HOST_URL) + "webServers/User/FbRegister";
    public static String emailRegister = (isDebug ? TEST_HOST_URL : HOST_URL) + "webServers/User/UserEmRegister";
    //修改密码
    public static String UserPasswordEdit = (isDebug ? TEST_HOST_URL : HOST_URL) + "webServers/User/UserPasswordEdit";

    //绑定手机号
    public static String UserBindingAccounts = (isDebug ? TEST_HOST_URL : HOST_URL) + "webServers/User/UserBindingAccounts";

    //登录
    public static String loginURl = (isDebug ? TEST_HOST_URL : HOST_URL) + "Webservers/User/Userlogin";
    //用户注册信息修改接口
    public static String userRegInfoFill = (isDebug ? TEST_HOST_URL : HOST_URL) + "WebServers/User/UserRegInfoFill";
    public static String userUploadPhotos = (isDebug ? TEST_HOST_URL : HOST_URL) + "Webservers/User/UserUploadPhotos";
    //房间首页
    public static String barIndex = (isDebug ? TEST_HOST_URL : HOST_URL) + "Webservers/Bar/BarIndex";
    //房间详细列表
    public static String BarIndexDetail = (isDebug ? TEST_HOST_URL : HOST_URL) + "Webservers/Bar/BarIndexDetail";
    //我的bar列表
    public static String BarInList = (isDebug ? TEST_HOST_URL : HOST_URL) + "Webservers/Messages/BarInList";
    //Search Bar
    public static String BarSearchTop = (isDebug ? TEST_HOST_URL : HOST_URL) + "Webservers/Bar/BarSearchTop";
    //搜索bar
    public static String searchBar = (isDebug ? TEST_HOST_URL : HOST_URL) + "Webservers/Bar/BarSearch";
    //创建bar
    public static String barCreate = (isDebug ? TEST_HOST_URL : HOST_URL) + "Webservers/Bar/BarCreate";
    //g关注bar
    public static String BarFollow = (isDebug ? TEST_HOST_URL : HOST_URL) + "Webservers/Bar/BarFollow";
    //标签列表
    public static String BarLables = (isDebug ? TEST_HOST_URL : HOST_URL) + "Webservers/Bar/BarLables";
    //房间分享到房间
    public static String BarShareDynamic = (isDebug ? TEST_HOST_URL : HOST_URL) + "Webservers/Bar/BarShareDynamic";
    //举报
    public static String BarReport = (isDebug ? TEST_HOST_URL : HOST_URL) + "Webservers/Bar/BarReport";
    //bar资料接口
    public static String BarInfo = (isDebug ? TEST_HOST_URL : HOST_URL) + "Webservers/Bar/BarInfo";
    //加入bar
    public static String BarJoin = (isDebug ? TEST_HOST_URL : HOST_URL) + "Webservers/Bar/BarJoin";
    //关注
    public static String FocusCtl = (isDebug ? TEST_HOST_URL : HOST_URL) + "Webservers/Messages/UserFollow";//?userid=10052&fuserid=10109&type=0
    public static String PersonerInfo = (isDebug ? TEST_HOST_URL : HOST_URL) + "Webservers/Messages/HeInformation";//?userid=10052&touserid=10109;
    //relations
    public static String RelationList = (isDebug ? TEST_HOST_URL : HOST_URL) + "Webservers/Messages/FriendMylist";
    //视频开播地址
    public static String BroadcastIns = (isDebug ? TEST_HOST_URL : HOST_URL) + "Webservers/Bar/BroadcastIns";
    //bar成员列表
    public static String BarMemberlist = (isDebug ? TEST_HOST_URL : HOST_URL) + "Webservers/Bar/BarMemberlist";
    //申请成员列表
    public static String JoinBarlist = (isDebug ? TEST_HOST_URL : HOST_URL) + "Webservers/Bar/JoinBarlist";
    //bar的排行
    public static String BarWeekRanking = (isDebug ? TEST_HOST_URL : HOST_URL) + "Webservers/Bar/BarWeekRanking";
    //成员管理操作
    public static String BarManagerOperation = (isDebug ? TEST_HOST_URL : HOST_URL) + "Webservers/Bar/BarManagerOperation";
    //房间个人资料修改提交
    public static String BarInfoEidt = (isDebug ? TEST_HOST_URL : HOST_URL) + "Webservers/Bar/BarInfoEidt";
    //创建bar 金币消耗界面
    public static String CreateBarBefore = (isDebug ? TEST_HOST_URL : HOST_URL) + "Webservers/Bar/CreateBarBefore";
    //升级bar消耗金币界面
    public static String BarUpgradeBarBefore = (isDebug ? TEST_HOST_URL : HOST_URL) + "Webservers/Bar/BarUpgradeBarBefore";
    //升级bar
    public static String BarUpgradeBar = (isDebug ? TEST_HOST_URL : HOST_URL) + "Webservers/Bar/BarUpgradeBar";
    //访客列表
    public static String UserVisitorLoglist = (isDebug ? TEST_HOST_URL : HOST_URL) + "Webservers/messages/UserVisitorLoglist";
    //访客点击
    public static String VistorClick = (isDebug ? TEST_HOST_URL : HOST_URL) + "Webservers/Messages/VisitorLogClick";
    //活动列表
    public static String ActivityList = (isDebug ? TEST_HOST_URL : HOST_URL) + "Webservers/Messages/ActivityList";
    //上传图片更新地址
    public static String PicUpload = (isDebug ? TEST_HOST_URL : HOST_URL) + "Webservers/Picture/PicUpload";
    //Agreement.html
    public static String Agreement = (isDebug ? TEST_HOST_URL : HOST_URL) + "web/Agreement.html";
    //领取工资，
    public static String BarWageGet = (isDebug ? TEST_HOST_URL : HOST_URL) + "Webservers/Bar/BarWageGet";
    //金币兑换
    public static String DiamondExCoin = (isDebug ? TEST_HOST_URL : HOST_URL) + "Webservers/Recharge/DiamondExCoin";

    //
    public static String BeAgencyUrl = "http://www.baidu.com";

    //下单
    public static String RechargeOrder = (isDebug ? TEST_HOST_URL : HOST_URL) + "Webservers/Recharge/RechargeOrder";
    public static String RechargeDiamondAdd = (isDebug ? TEST_HOST_URL : HOST_URL) + "Webservers/Recharge/RechargeDiamondAdd";

    //user bill
    public static String UserBill = (isDebug ? TEST_HOST_URL : HOST_URL) + "Webservers/Recharge/TransactionDetails";
    // phone bind check
    public static String UserPhoneBinded = (isDebug ? TEST_HOST_URL : HOST_URL) + "Webservers/User/UserBindingCheck";
    public static String UserSearch = (isDebug ? TEST_HOST_URL : HOST_URL) + "Webservers/Messages/UserSearch";

    //获取配置版本信息
    public static String version = (isDebug ? TEST_HOST_URL : HOST_URL) + "Webservers/User/VerInfo";

    //count统计
    public static String PlatformIntoLogIns = (isDebug ? TEST_HOST_URL : HOST_URL) + "Webservers/User/PlatformIntoLogIns";
    //充值接口
    public static String RechargeConfigList = (isDebug ? TEST_HOST_URL : HOST_URL) + "Webservers/Recharge/RechargeConfigList";

    //礼物xml地址
    public static String GiftXmlUrl = (isDebug ? TEST_HOST_URL : HOST_URL) + "Upload/data.xml";

    public static String MessageXmlUrl = (isDebug ? TEST_HOST_URL : HOST_URL) + "Upload/message.xml";
}

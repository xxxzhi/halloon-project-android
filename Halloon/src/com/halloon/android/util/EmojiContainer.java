package com.halloon.android.util;

import android.content.Context;

public class EmojiContainer {
	
	public static int getEmojiId(Context context, int i) {
		String name;
		if (i < 9) {
			name = "h00" + (i + 1);
		} else if (i < 99) {
			name = "h0" + (i + 1);
		} else {
			name = "h" + (i + 1);
		}

		return context.getResources().getIdentifier(context.getPackageName() + ":drawable/" + name, null, null);
	}
	
	public static final String[] emoNameContainer;

	static {
		String[] arrayOfString = new String[105];
		arrayOfString[0] = "微笑";
		arrayOfString[1] = "撇嘴";
		arrayOfString[2] = "色";
		arrayOfString[3] = "发呆";
		arrayOfString[4] = "得意";
		arrayOfString[5] = "流泪";
		arrayOfString[6] = "害羞";
		arrayOfString[7] = "闭嘴";
		arrayOfString[8] = "睡";
		arrayOfString[9] = "大哭";
		arrayOfString[10] = "尴尬";
		arrayOfString[11] = "发怒";
		arrayOfString[12] = "调皮";
		arrayOfString[13] = "呲牙";
		arrayOfString[14] = "惊讶";
		arrayOfString[15] = "难过";
		arrayOfString[16] = "酷";
		arrayOfString[17] = "冷汗";
		arrayOfString[18] = "抓狂";
		arrayOfString[19] = "吐";
		arrayOfString[20] = "偷笑";
		arrayOfString[21] = "可爱";
		arrayOfString[22] = "白眼";
		arrayOfString[23] = "傲慢";
		arrayOfString[24] = "饥饿";
		arrayOfString[25] = "困";
		arrayOfString[26] = "惊恐";
		arrayOfString[27] = "流汗";
		arrayOfString[28] = "憨笑";
		arrayOfString[29] = "大兵";
		arrayOfString[30] = "奋斗";
		arrayOfString[31] = "咒骂";
		arrayOfString[32] = "疑问";
		arrayOfString[33] = "嘘";
		arrayOfString[34] = "晕";
		arrayOfString[35] = "折磨";
		arrayOfString[36] = "衰";
		arrayOfString[37] = "骷髅";
		arrayOfString[38] = "敲打";
		arrayOfString[39] = "再见";
		arrayOfString[40] = "擦汗";
		arrayOfString[41] = "抠鼻";
		arrayOfString[42] = "鼓掌";
		arrayOfString[43] = "糗大了";
		arrayOfString[44] = "坏笑";
		arrayOfString[45] = "左哼哼";
		arrayOfString[46] = "右哼哼";
		arrayOfString[47] = "哈欠";
		arrayOfString[48] = "鄙视";
		arrayOfString[49] = "委屈";
		arrayOfString[50] = "快哭了";
		arrayOfString[51] = "阴险";
		arrayOfString[52] = "亲亲";
		arrayOfString[53] = "吓";
		arrayOfString[54] = "可怜";
		arrayOfString[55] = "菜刀";
		arrayOfString[56] = "西瓜";
		arrayOfString[57] = "啤酒";
		arrayOfString[58] = "篮球";
		arrayOfString[59] = "乒乓";
		arrayOfString[60] = "咖啡";
		arrayOfString[61] = "饭";
		arrayOfString[62] = "猪头";
		arrayOfString[63] = "玫瑰";
		arrayOfString[64] = "凋谢";
		arrayOfString[65] = "示爱";
		arrayOfString[66] = "爱心";
		arrayOfString[67] = "心碎";
		arrayOfString[68] = "蛋糕";
		arrayOfString[69] = "闪电";
		arrayOfString[70] = "炸弹";
		arrayOfString[71] = "刀";
		arrayOfString[72] = "足球";
		arrayOfString[73] = "瓢虫";
		arrayOfString[74] = "便便";
		arrayOfString[75] = "月亮";
		arrayOfString[76] = "太阳";
		arrayOfString[77] = "礼物";
		arrayOfString[78] = "拥抱";
		arrayOfString[79] = "强";
		arrayOfString[80] = "弱";
		arrayOfString[81] = "握手";
		arrayOfString[82] = "胜利";
		arrayOfString[83] = "抱拳";
		arrayOfString[84] = "勾引";
		arrayOfString[85] = "拳头";
		arrayOfString[86] = "差劲";
		arrayOfString[87] = "爱你";
		arrayOfString[88] = "NO";
		arrayOfString[89] = "OK";
		arrayOfString[90] = "爱情";
		arrayOfString[91] = "飞吻";
		arrayOfString[92] = "跳跳";
		arrayOfString[93] = "发抖";
		arrayOfString[94] = "怄火";
		arrayOfString[95] = "转圈";
		arrayOfString[96] = "磕头";
		arrayOfString[97] = "回头";
		arrayOfString[98] = "跳绳";
		arrayOfString[99] = "挥手";
		arrayOfString[100] = "激动";
		arrayOfString[101] = "街舞";
		arrayOfString[102] = "献吻";
		arrayOfString[103] = "左太极";
		arrayOfString[104] = "右太极";

		emoNameContainer = arrayOfString;
	}
}

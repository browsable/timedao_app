package com.daemin.enumclass;

import android.graphics.Canvas;

/**
 * Created by hernia on 2015-06-27.
 */
public enum TimePos {
    P0101(1, 1), P0301(3, 1), P0501(5, 1), P0701(7, 1), P0901(9, 1), P1101(11, 1), P1301(13, 1),
    P0102(1, 2), P0302(3, 2), P0502(5, 2), P0702(7, 2), P0902(9, 2), P1102(11, 2), P1302(13, 2),
    P0103(1, 3), P0303(3, 3), P0503(5, 3), P0703(7, 3), P0903(9, 3), P1103(11, 3), P1303(13, 3),
    P0104(1, 4), P0304(3, 4), P0504(5, 4), P0704(7, 4), P0904(9, 4), P1104(11, 4), P1304(13, 4),
    P0105(1, 5), P0305(3, 5), P0505(5, 5), P0705(7, 5), P0905(9, 5), P1105(11, 5), P1305(13, 5),
    P0106(1, 6), P0306(3, 6), P0506(5, 6), P0706(7, 6), P0906(9, 6), P1106(11, 6), P1306(13, 6),
    P0107(1, 7), P0307(3, 7), P0507(5, 7), P0707(7, 7), P0907(9, 7), P1107(11, 7), P1307(13, 7),
    P0108(1, 8), P0308(3, 8), P0508(5, 8), P0708(7, 8), P0908(9, 8), P1108(11, 8), P1308(13, 8),
    P0109(1, 9), P0309(3, 9), P0509(5, 9), P0709(7, 9), P0909(9, 9), P1109(11, 9), P1309(13, 9),
    P0110(1, 10), P0310(3, 10), P0510(5, 10), P0710(7, 10), P0910(9, 10), P1110(11, 10), P1310(13, 10),
    P0111(1, 11), P0311(3, 11), P0511(5, 11), P0711(7, 11), P0911(9, 11), P1111(11, 11), P1311(13, 11),
    P0112(1, 12), P0312(3, 12), P0512(5, 12), P0712(7, 12), P0912(9, 12), P1112(11, 12), P1312(13, 12),
    P0113(1, 13), P0313(3, 13), P0513(5, 13), P0713(7, 13), P0913(9, 13), P1113(11, 13), P1313(13, 13),
    P0114(1, 14), P0314(3, 14), P0514(5, 14), P0714(7, 14), P0914(9, 14), P1114(11, 14), P1314(13, 14),
    P0115(1, 15), P0315(3, 15), P0515(5, 15), P0715(7, 15), P0915(9, 15), P1115(11, 15), P1315(13, 15),
    P0116(1, 16), P0316(3, 16), P0516(5, 16), P0716(7, 16), P0916(9, 16), P1116(11, 16), P1316(13, 16),
    P0117(1, 17), P0317(3, 17), P0517(5, 17), P0717(7, 17), P0917(9, 17), P1117(11, 17), P1317(13, 17),
    P0118(1, 18), P0318(3, 18), P0518(5, 18), P0718(7, 18), P0918(9, 18), P1118(11, 18), P1318(13, 18),
    P0119(1, 19), P0319(3, 19), P0519(5, 19), P0719(7, 19), P0919(9, 19), P1119(11, 19), P1319(13, 19),
    P0120(1, 20), P0320(3, 20), P0520(5, 20), P0720(7, 20), P0920(9, 20), P1120(11, 20), P1320(13, 20),
    P0121(1, 21), P0321(3, 21), P0521(5, 21), P0721(7, 21), P0921(9, 21), P1121(11, 21), P1321(13, 21),
    P0122(1, 22), P0322(3, 22), P0522(5, 22), P0722(7, 22), P0922(9, 22), P1122(11, 22), P1322(13, 22),
    P0123(1, 23), P0323(3, 23), P0523(5, 23), P0723(7, 23), P0923(9, 23), P1123(11, 23), P1323(13, 23),
    P0124(1, 24), P0324(3, 24), P0524(5, 24), P0724(7, 24), P0924(9, 24), P1124(11, 24), P1324(13, 24),
    P0125(1, 25), P0325(3, 25), P0525(5, 25), P0725(7, 25), P0925(9, 25), P1125(11, 25), P1325(13, 25),
    P0126(1, 26), P0326(3, 26), P0526(5, 26), P0726(7, 26), P0926(9, 26), P1126(11, 26), P1326(13, 26),
    P0127(1, 27), P0327(3, 27), P0527(5, 27), P0727(7, 27), P0927(9, 27), P1127(11, 27), P1327(13, 27),
    P0128(1, 28), P0328(3, 28), P0528(5, 28), P0728(7, 28), P0928(9, 28), P1128(11, 28), P1328(13, 28),
    P0129(1, 29), P0329(3, 29), P0529(5, 29), P0729(7, 29), P0929(9, 29), P1129(11, 29), P1329(13, 29),
    P0130(1, 30), P0330(3, 30), P0530(5, 30), P0730(7, 30), P0930(9, 30), P1130(11, 30), P1330(13, 30);

    private PosState posState;
    private int xth;
    private int yth;

    TimePos() {
    }

    TimePos(int xth, int yth) {
        this.xth = xth;
        this.yth = yth;
        this.posState = PosState.NO_PAINT;
    }

    public int getXth() {
        return xth;
    }

    public int getYth() {
        return yth;
    }

    public void setPosState(PosState posState) {
        this.posState = posState;
    }

    public PosState getPosState() {
        return posState;
    }

    public void drawTimePos(Canvas canvas, int width, int height) {
        posState.drawTimePos(canvas, width, height, xth, yth);
    }
}
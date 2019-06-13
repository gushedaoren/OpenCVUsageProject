package com.serenegiant.opencvusageproject;


import android.util.Log;

import org.opencv.core.Mat;

/**
 * 二值化算法集
 */
public class BinaryZation {





    public static int ChooseMethod(Mat mat, int pos) {
        byte []datas = new byte[mat.cols()*mat.rows()];
        mat.get(0,0,datas);
        int []pixels = new int[mat.cols()*mat.rows()];
        for(int i=0;i<datas.length;i++){
            if (datas[i] >= 0) {
                pixels[i] = datas[i];
            }else{
                pixels[i] = 256+datas[i];
            }
        }


        int []HistGram = new int[256];
        for(int i=0;i<pixels.length;i++){
            HistGram[pixels[i]]++;
        }
        int thresold = -1;
        switch (pos) {
            case 1:
                thresold = GetMeanThreshold(HistGram);
                break;
            case 2:
                thresold = GetMinimumThreshold(HistGram);
                break;
            case 3:
                thresold = GetIntermodesThreshold(HistGram);
                break;
            case 4:
                thresold = GetIterativeBestThreshold(HistGram);
                break;
            case 5:
                thresold = GetOSTUThreshold(HistGram);
                break;
            case 6:
                thresold = Get1DMaxEntropyThreshold(HistGram);
                break;
            case 7:
                thresold = GetMomentPreservingThreshold(HistGram);
                break;
            case 8:
                thresold = GetHuangFuzzyThreshold(HistGram);
                break;
            case 9:
                thresold = GetKittlerMinError(HistGram);
                break;
            case 10:
                thresold = GetIsoDataThreshold(HistGram);
                break;
            case 11:
                thresold = GetShanbhagThreshold(HistGram);
                break;
            case 12:
                thresold = GetYenThreshold(HistGram);
                break;

        }
        return thresold;
    }

    /**
     * 平均值二值化，选取最大和最小的平均值作为阈值
     *
     * @param HistGram 直方图
     * @return 阈值
     */
    public static int GetMeanThreshold(int[] HistGram) {
        int Sum = 0, Amount = 0;
        for (int Y = 0; Y < 256; Y++) {
            Amount += HistGram[Y];
            Sum += Y * HistGram[Y];
        }
        return Sum / Amount;
    }


    /**
     * 半分比阈值二值化
     *
     * @param pixels 点
     * @param Tile     百分比
     * @return 阈值
     */
    public static int GetPTileThreshold(int[] pixels, int Tile) {
        int []HistGram = new int[256];
        for(int i=0;i<pixels.length;i++){
            HistGram[pixels[i]]++;
        }
        int Y, Amount = 0, Sum = 0;
        for (Y = 0; Y < 256; Y++) Amount += HistGram[Y];        //  像素总数
        for (Y = 0; Y < 256; Y++) {
            Sum = Sum + HistGram[Y];
            if (Sum >= Amount * Tile / 100) return Y;
        }
        return -1;
    }


    /**
     * 双峰谷底最小值二值化
     *
     * @param HistGram 直方图
     * @return 阈值
     */
    public static int GetMinimumThreshold(int[] HistGram) {
        int Y, Iter = 0;
        double[] HistGramC = new double[256];           // 基于精度问题，一定要用浮点数来处理，否则得不到正确的结果
        double[] HistGramCC = new double[256];          // 求均值的过程会破坏前面的数据，因此需要两份数据
        for (Y = 0; Y < 256; Y++) {
            HistGramC[Y] = HistGram[Y];
            HistGramCC[Y] = HistGram[Y];
        }

        // 通过三点求均值来平滑直方图
        while (IsDimodal(HistGramCC) == false)                                        // 判断是否已经是双峰的图像了
        {
            HistGramCC[0] = (HistGramC[0] + HistGramC[0] + HistGramC[1]) / 3;                 // 第一点
            for (Y = 1; Y < 255; Y++)
                HistGramCC[Y] = (HistGramC[Y - 1] + HistGramC[Y] + HistGramC[Y + 1]) / 3;     // 中间的点
            HistGramCC[255] = (HistGramC[254] + HistGramC[255] + HistGramC[255]) / 3;         // 最后一点
            for (int i = 0; i < HistGramCC.length; i++) {
                HistGramC[i] = HistGramCC[i];
            }
            Iter++;
            if (Iter >= 1000)
                return -1;                                                   // 直方图无法平滑为双峰的，返回错误代码
        }
        // 阈值极为两峰之间的最小值
        boolean Peakfound = false;
        for (Y = 1; Y < 255; Y++) {
            if (HistGramCC[Y - 1] < HistGramCC[Y] && HistGramCC[Y + 1] < HistGramCC[Y])
                Peakfound = true;
            if (Peakfound == true && HistGramCC[Y - 1] >= HistGramCC[Y] && HistGramCC[Y + 1] >= HistGramCC[Y])
                return Y - 1;
        }
        return -1;
    }

    private static boolean IsDimodal(double[] HistGram)       // 检测直方图是否为双峰的
    {
        // 对直方图的峰进行计数，只有峰数位2才为双峰
        int Count = 0;
        for (int Y = 1; Y < 255; Y++) {
            if (HistGram[Y - 1] < HistGram[Y] && HistGram[Y + 1] < HistGram[Y]) {
                Count++;
                if (Count > 2) return false;
            }
        }
        if (Count == 2)
            return true;
        else
            return false;
    }


    /**
     * 双峰平均值二值化
     *
     * @param HistGram 直方图
     * @return 阈值
     */
    public static int GetIntermodesThreshold(int[] HistGram) {
        int Y, Iter = 0, Index;
        double[] HistGramC = new double[256];           // 基于精度问题，一定要用浮点数来处理，否则得不到正确的结果
        double[] HistGramCC = new double[256];          // 求均值的过程会破坏前面的数据，因此需要两份数据
        for (Y = 0; Y < 256; Y++) {
            HistGramC[Y] = HistGram[Y];
            HistGramCC[Y] = HistGram[Y];
        }
        // 通过三点求均值来平滑直方图
        while (IsDimodal(HistGramCC) == false)                                                  // 判断是否已经是双峰的图像了
        {
            HistGramCC[0] = (HistGramC[0] + HistGramC[0] + HistGramC[1]) / 3;                   // 第一点
            for (Y = 1; Y < 255; Y++)
                HistGramCC[Y] = (HistGramC[Y - 1] + HistGramC[Y] + HistGramC[Y + 1]) / 3;       // 中间的点
            HistGramCC[255] = (HistGramC[254] + HistGramC[255] + HistGramC[255]) / 3;           // 最后一点
            for (int i = 0; i < HistGramCC.length; i++) {
                HistGramC[i] = HistGramCC[i];
            }
            ;         // 备份数据，为下一次迭代做准备
            Iter++;
            if (Iter >= 10000)
                return -1;                                                       // 似乎直方图无法平滑为双峰的，返回错误代码
        }
        // 阈值为两峰值的平均值
        int[] Peak = new int[2];
        for (Y = 1, Index = 0; Y < 255; Y++)
            if (HistGramCC[Y - 1] < HistGramCC[Y] && HistGramCC[Y + 1] < HistGramCC[Y])
                Peak[Index++] = Y - 1;
        return ((Peak[0] + Peak[1]) / 2);
    }


    /**
     * 迭代最佳阈值二值化
     *
     * @param HistGram 直方图
     * @return 阈值
     */
    public static int GetIterativeBestThreshold(int[] HistGram) {
        if(HistGram[0] == 183){
            Log.i("HistGram",HistGram[0]+"");
        }
        int X, Iter = 0;
        int MeanValueOne, MeanValueTwo, SumOne, SumTwo, SumIntegralOne, SumIntegralTwo;
        int MinValue, MaxValue;
        int Threshold, NewThreshold;

        for (MinValue = 0; MinValue < 256 && HistGram[MinValue] == 0; MinValue++) ;
        for (MaxValue = 255; MaxValue > MinValue && HistGram[MaxValue] == 0; MaxValue--) ;

        if (MaxValue == MinValue) return MaxValue;          // 图像中只有一个颜色
        if (MinValue + 1 == MaxValue) return MinValue;      // 图像中只有二个颜色

        Threshold = MinValue;
        NewThreshold = (MaxValue + MinValue) >> 1;
        while (Threshold != NewThreshold)    // 当前后两次迭代的获得阈值相同时，结束迭代
        {
            SumOne = 0;
            SumIntegralOne = 0;
            SumTwo = 0;
            SumIntegralTwo = 0;
            Threshold = NewThreshold;
            for (X = MinValue; X <= Threshold; X++)         //根据阈值将图像分割成目标和背景两部分，求出两部分的平均灰度值
            {
                SumIntegralOne += HistGram[X] * X;
                SumOne += HistGram[X];
            }
            MeanValueOne = SumIntegralOne / SumOne;
            for (X = Threshold + 1; X <= MaxValue; X++) {
                SumIntegralTwo += HistGram[X] * X;
                SumTwo += HistGram[X];
            }
            MeanValueTwo = SumIntegralTwo / SumTwo;
            NewThreshold = (MeanValueOne + MeanValueTwo) >> 1;       //求出新的阈值
            Iter++;
            if (Iter >= 1000) return -1;
        }
        return Threshold;
    }


    /**
     * OSTU大律法
     *
     * @param HistGram
     * @return
     */
    public static int GetOSTUThreshold(int[] HistGram) {
        int X, Y, Amount = 0;
        int PixelBack = 0, PixelFore = 0, PixelIntegralBack = 0, PixelIntegralFore = 0, PixelIntegral = 0;
        double OmegaBack, OmegaFore, MicroBack, MicroFore, SigmaB, Sigma;              // 类间方差;
        int MinValue, MaxValue;
        int Threshold = 0;

        for (MinValue = 0; MinValue < 256 && HistGram[MinValue] == 0; MinValue++) ;
        for (MaxValue = 255; MaxValue > MinValue && HistGram[MaxValue] == 0; MaxValue--) ;
        if (MaxValue == MinValue) return MaxValue;          // 图像中只有一个颜色
        if (MinValue + 1 == MaxValue) return MinValue;      // 图像中只有二个颜色

        for (Y = MinValue; Y <= MaxValue; Y++) Amount += HistGram[Y];        //  像素总数

        PixelIntegral = 0;
        for (Y = MinValue; Y <= MaxValue; Y++) PixelIntegral += HistGram[Y] * Y;
        SigmaB = -1;
        for (Y = MinValue; Y < MaxValue; Y++) {
            PixelBack = PixelBack + HistGram[Y];
            PixelFore = Amount - PixelBack;
            OmegaBack = (double) PixelBack / Amount;
            OmegaFore = (double) PixelFore / Amount;
            PixelIntegralBack += HistGram[Y] * Y;
            PixelIntegralFore = PixelIntegral - PixelIntegralBack;
            MicroBack = (double) PixelIntegralBack / PixelBack;
            MicroFore = (double) PixelIntegralFore / PixelFore;
            Sigma = OmegaBack * OmegaFore * (MicroBack - MicroFore) * (MicroBack - MicroFore);
            if (Sigma > SigmaB) {
                SigmaB = Sigma;
                Threshold = Y;
            }
        }
        return Threshold;
    }


    /**
     * 一维最大熵
     *
     * @param HistGram
     * @return
     */
    public static int Get1DMaxEntropyThreshold(int[] HistGram) {
        int X, Y, Amount = 0;
        double[] HistGramD = new double[256];
        double SumIntegral, EntropyBack, EntropyFore, MaxEntropy;
        int MinValue = 255, MaxValue = 0;
        int Threshold = 0;

        for (MinValue = 0; MinValue < 256 && HistGram[MinValue] == 0; MinValue++) ;
        for (MaxValue = 255; MaxValue > MinValue && HistGram[MinValue] == 0; MaxValue--) ;
        if (MaxValue == MinValue) return MaxValue;          // 图像中只有一个颜色
        if (MinValue + 1 == MaxValue) return MinValue;      // 图像中只有二个颜色

        for (Y = MinValue; Y <= MaxValue; Y++) Amount += HistGram[Y];        //  像素总数

        for (Y = MinValue; Y <= MaxValue; Y++) HistGramD[Y] = (double) HistGram[Y] / Amount + 1e-17;

        MaxEntropy = Double.MIN_VALUE;
        ;
        for (Y = MinValue + 1; Y < MaxValue; Y++) {
            SumIntegral = 0;
            for (X = MinValue; X <= Y; X++) SumIntegral += HistGramD[X];
            EntropyBack = 0;
            for (X = MinValue; X <= Y; X++)
                EntropyBack += (-HistGramD[X] / SumIntegral * Math.log(HistGramD[X] / SumIntegral));
            EntropyFore = 0;
            for (X = Y + 1; X <= MaxValue; X++)
                EntropyFore += (-HistGramD[X] / (1 - SumIntegral) * Math.log(HistGramD[X] / (1 - SumIntegral)));
            if (MaxEntropy < EntropyBack + EntropyFore) {
                Threshold = Y;
                MaxEntropy = EntropyBack + EntropyFore;
            }
        }
        return Threshold;
    }


    /**
     * 力矩保持法
     *
     * @param HistGram
     * @return
     */
    public static byte GetMomentPreservingThreshold(int[] HistGram) {
        int X, Y, Index = 0, Amount = 0;
        double[] Avec = new double[256];
        double X2, X1, X0, Min;

        for (Y = 0; Y <= 255; Y++) Amount += HistGram[Y];        //  像素总数
        for (Y = 0; Y < 256; Y++)
            Avec[Y] = (double) A(HistGram, Y) / Amount;       // The threshold is chosen such that A(y,t)/A(y,n) is closest to x0.

        // The following finds x0.

        X2 = (double) (B(HistGram, 255) * C(HistGram, 255) - A(HistGram, 255) * D(HistGram, 255)) / (double) (A(HistGram, 255) * C(HistGram, 255) - B(HistGram, 255) * B(HistGram, 255));
        X1 = (double) (B(HistGram, 255) * D(HistGram, 255) - C(HistGram, 255) * C(HistGram, 255)) / (double) (A(HistGram, 255) * C(HistGram, 255) - B(HistGram, 255) * B(HistGram, 255));
        X0 = 0.5 - (B(HistGram, 255) / A(HistGram, 255) + X2 / 2) / Math.sqrt(X2 * X2 - 4 * X1);

        for (Y = 0, Min = Double.MIN_VALUE; Y < 256; Y++) {
            if (Math.abs(Avec[Y] - X0) < Min) {
                Min = Math.abs(Avec[Y] - X0);
                Index = Y;
            }
        }
        return (byte) Index;
    }


    /**
     * 基于模糊集理论的二值化算法 过慢
     *
     * @param HistGram
     * @return
     */
    public static int GetHuangFuzzyThreshold(int[] HistGram) {
        int X, Y;
        int First, Last;
        int Threshold = -1;
        double BestEntropy = Double.MAX_VALUE, Entropy;
        //   找到第一个和最后一个非0的色阶值
        for (First = 0; First < HistGram.length && HistGram[First] == 0; First++) ;
        for (Last = HistGram.length - 1; Last > First && HistGram[Last] == 0; Last--) ;
        if (First == Last) return First;                // 图像中只有一个颜色
        if (First + 1 == Last) return First;            // 图像中只有二个颜色

        // 计算累计直方图以及对应的带权重的累计直方图
        int[] S = new int[Last + 1];
        int[] W = new int[Last + 1];            // 对于特大图，此数组的保存数据可能会超出int的表示范围，可以考虑用long类型来代替
        S[0] = HistGram[0];
        for (Y = First > 1 ? First : 1; Y <= Last; Y++) {
            S[Y] = S[Y - 1] + HistGram[Y];
            W[Y] = W[Y - 1] + Y * HistGram[Y];
        }

        // 建立公式（4）及（6）所用的查找表
        double[] Smu = new double[Last + 1 - First];
        for (Y = 1; Y < Smu.length; Y++) {
            double mu = 1 / (1 + (double) Y / (Last - First));               // 公式（4）
            Smu[Y] = -mu * Math.log(mu) - (1 - mu) * Math.log(1 - mu);      // 公式（6）
        }

        // 迭代计算最佳阈值
        for (Y = First; Y <= Last; Y++) {
            Entropy = 0;
            int mu = (int) Math.round((double) W[Y] / S[Y]);             // 公式17
            for (X = First; X <= Y; X++)
                Entropy += Smu[Math.abs(X - mu)] * HistGram[X];
            mu = (int) Math.round((double) (W[Last] - W[Y]) / (S[Last] - S[Y]));  // 公式18
            for (X = Y + 1; X <= Last; X++)
                Entropy += Smu[Math.abs(X - mu)] * HistGram[X];       // 公式8
            if (BestEntropy > Entropy) {
                BestEntropy = Entropy;      // 取最小熵处为最佳阈值
                Threshold = Y;
            }
        }
        return Threshold;
    }


    /**
     * Kittler最小错误分类法
     *
     * @param HistGram
     * @return
     */
    public static int GetKittlerMinError(int[] HistGram) {
        int X, Y;
        int MinValue, MaxValue;
        int Threshold;
        int PixelBack, PixelFore;
        double OmegaBack, OmegaFore, MinSigma, Sigma, SigmaBack, SigmaFore;
        for (MinValue = 0; MinValue < 256 && HistGram[MinValue] == 0; MinValue++) ;
        for (MaxValue = 255; MaxValue > MinValue && HistGram[MinValue] == 0; MaxValue--) ;
        if (MaxValue == MinValue) return MaxValue;          // 图像中只有一个颜色
        if (MinValue + 1 == MaxValue) return MinValue;      // 图像中只有二个颜色
        Threshold = -1;
        MinSigma = 1E+20;
        for (Y = MinValue; Y < MaxValue; Y++) {
            PixelBack = 0;
            PixelFore = 0;
            OmegaBack = 0;
            OmegaFore = 0;
            for (X = MinValue; X <= Y; X++) {
                PixelBack += HistGram[X];
                OmegaBack = OmegaBack + X * HistGram[X];
            }
            for (X = Y + 1; X <= MaxValue; X++) {
                PixelFore += HistGram[X];
                OmegaFore = OmegaFore + X * HistGram[X];
            }
            OmegaBack = OmegaBack / PixelBack;
            OmegaFore = OmegaFore / PixelFore;
            SigmaBack = 0;
            SigmaFore = 0;
            for (X = MinValue; X <= Y; X++)
                SigmaBack = SigmaBack + (X - OmegaBack) * (X - OmegaBack) * HistGram[X];
            for (X = Y + 1; X <= MaxValue; X++)
                SigmaFore = SigmaFore + (X - OmegaFore) * (X - OmegaFore) * HistGram[X];
            if (SigmaBack == 0 || SigmaFore == 0) {
                if (Threshold == -1)
                    Threshold = Y;
            } else {
                SigmaBack = Math.sqrt(SigmaBack / PixelBack);
                SigmaFore = Math.sqrt(SigmaFore / PixelFore);
                Sigma = 1 + 2 * (PixelBack * Math.log(SigmaBack / PixelBack) + PixelFore * Math.log(SigmaFore / PixelFore));
                if (Sigma < MinSigma) {
                    MinSigma = Sigma;
                    Threshold = Y;
                }
            }
        }
        return Threshold;
    }


    /**
     * ISODATA(也叫做intermeans法） 过慢
     *
     * @param HistGram
     * @return
     */
    public static int GetIsoDataThreshold(int[] HistGram) {
        int i, l, toth, totl, h, g = 0;
        for (i = 1; i < HistGram.length; i++) {
            if (HistGram[i] > 0) {
                g = i + 1;
                break;
            }
        }
        while (true) {
            l = 0;
            totl = 0;
            for (i = 0; i < g; i++) {
                totl = totl + HistGram[i];
                l = l + (HistGram[i] * i);
            }
            h = 0;
            toth = 0;
            for (i = g + 1; i < HistGram.length; i++) {
                toth += HistGram[i];
                h += (HistGram[i] * i);
            }
            if (totl > 0 && toth > 0) {
                l /= totl;
                h /= toth;
                if (g == (int) Math.round((l + h) / 2.0))
                    break;
            }
            g++;
            if (g > HistGram.length - 2) {
                return 0;
            }
        }
        return g;
    }


    /**
     * Shanbhag 法  过慢
     *
     * @param HistGram
     * @return
     */
    public static int GetShanbhagThreshold(int[] HistGram) {
        int threshold;
        int ih, it;
        int first_bin;
        int last_bin;
        double term;
        double tot_ent;  /* total entropy */
        double min_ent;  /* max entropy */
        double ent_back; /* entropy of the background pixels at a given threshold */
        double ent_obj;  /* entropy of the object pixels at a given threshold */
        double[] norm_histo = new double[HistGram.length]; /* normalized histogram */
        double[] P1 = new double[HistGram.length]; /* cumulative normalized histogram */
        double[] P2 = new double[HistGram.length];

        int total = 0;
        for (ih = 0; ih < HistGram.length; ih++)
            total += HistGram[ih];

        for (ih = 0; ih < HistGram.length; ih++)
            norm_histo[ih] = (double) HistGram[ih] / total;

        P1[0] = norm_histo[0];
        P2[0] = 1.0 - P1[0];
        for (ih = 1; ih < HistGram.length; ih++) {
            P1[ih] = P1[ih - 1] + norm_histo[ih];
            P2[ih] = 1.0 - P1[ih];
        }

        /* Determine the first non-zero bin */
        first_bin = 0;
        for (ih = 0; ih < HistGram.length; ih++) {
            if (!(Math.abs(P1[ih]) < 2.220446049250313E-16)) {
                first_bin = ih;
                break;
            }
        }

        /* Determine the last non-zero bin */
        last_bin = HistGram.length - 1;
        for (ih = HistGram.length - 1; ih >= first_bin; ih--) {
            if (!(Math.abs(P2[ih]) < 2.220446049250313E-16)) {
                last_bin = ih;
                break;
            }
        }

        // Calculate the total entropy each gray-level
        // and find the threshold that maximizes it 
        threshold = -1;
        min_ent = Double.MAX_VALUE;

        for (it = first_bin; it <= last_bin; it++) {
            /* Entropy of the background pixels */
            ent_back = 0.0;
            term = 0.5 / P1[it];
            for (ih = 1; ih <= it; ih++) { //0+1?
                ent_back -= norm_histo[ih] * Math.log(1.0 - term * P1[ih - 1]);
            }
            ent_back *= term;

            /* Entropy of the object pixels */
            ent_obj = 0.0;
            term = 0.5 / P2[it];
            for (ih = it + 1; ih < HistGram.length; ih++) {
                ent_obj -= norm_histo[ih] * Math.log(1.0 - term * P2[ih]);
            }
            ent_obj *= term;

            /* Total entropy */
            tot_ent = Math.abs(ent_back - ent_obj);

            if (tot_ent < min_ent) {
                min_ent = tot_ent;
                threshold = it;
            }
        }
        return threshold;
    }


    /**
     * Yen法
     *
     * @param HistGram
     * @return
     */
    public static int GetYenThreshold(int[] HistGram) {
        int threshold;
        int ih, it;
        double crit;
        double max_crit;
        double[] norm_histo = new double[HistGram.length]; /* normalized histogram */
        double[] P1 = new double[HistGram.length]; /* cumulative normalized histogram */
        double[] P1_sq = new double[HistGram.length];
        double[] P2_sq = new double[HistGram.length];

        int total = 0;
        for (ih = 0; ih < HistGram.length; ih++)
            total += HistGram[ih];

        for (ih = 0; ih < HistGram.length; ih++)
            norm_histo[ih] = (double) HistGram[ih] / total;

        P1[0] = norm_histo[0];
        for (ih = 1; ih < HistGram.length; ih++)
            P1[ih] = P1[ih - 1] + norm_histo[ih];

        P1_sq[0] = norm_histo[0] * norm_histo[0];
        for (ih = 1; ih < HistGram.length; ih++)
            P1_sq[ih] = P1_sq[ih - 1] + norm_histo[ih] * norm_histo[ih];

        P2_sq[HistGram.length - 1] = 0.0;
        for (ih = HistGram.length - 2; ih >= 0; ih--)
            P2_sq[ih] = P2_sq[ih + 1] + norm_histo[ih + 1] * norm_histo[ih + 1];

        /* Find the threshold that maximizes the criterion */
        threshold = -1;
        max_crit = Double.MIN_VALUE;
        for (it = 0; it < HistGram.length; it++) {
            crit = -1.0 * ((P1_sq[it] * P2_sq[it]) > 0.0 ? Math.log(P1_sq[it] * P2_sq[it]) : 0.0) + 2 * ((P1[it] * (1.0 - P1[it])) > 0.0 ? Math.log(P1[it] * (1.0 - P1[it])) : 0.0);
            if (crit > max_crit) {
                max_crit = crit;
                threshold = it;
            }
        }
        return threshold;
    }


    private static double A(int[] HistGram, int Index) {
        double Sum = 0;
        for (int Y = 0; Y <= Index; Y++)
            Sum += HistGram[Y];
        return Sum;
    }

    private static double B(int[] HistGram, int Index) {
        double Sum = 0;
        for (int Y = 0; Y <= Index; Y++)
            Sum += (double) Y * HistGram[Y];
        return Sum;
    }

    private static double C(int[] HistGram, int Index) {
        double Sum = 0;
        for (int Y = 0; Y <= Index; Y++)
            Sum += (double) Y * Y * HistGram[Y];
        return Sum;
    }

    private static double D(int[] HistGram, int Index) {
        double Sum = 0;
        for (int Y = 0; Y <= Index; Y++)
            Sum += (double) Y * Y * Y * HistGram[Y];
        return Sum;
    }
}

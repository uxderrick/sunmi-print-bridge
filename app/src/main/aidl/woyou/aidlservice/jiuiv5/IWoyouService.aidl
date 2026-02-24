package woyou.aidlservice.jiuiv5;

import woyou.aidlservice.jiuiv5.ICallback;

interface IWoyouService {
    void printerInit(ICallback callback);
    void printerSelfChecking(ICallback callback);

    int updatePrinterState();

    void setAlignment(int alignment, ICallback callback);
    void setFontSize(float fontsize, ICallback callback);

    void printText(String text, ICallback callback);
    void printTextWithFont(String text, String typeface, float fontsize, ICallback callback);

    void printBarCode(String data, int symbology, int height, int width, int textposition, ICallback callback);
    void printQRCode(String data, int modulesize, int errorlevel, ICallback callback);

    void lineWrap(int n, ICallback callback);

    void sendRAWData(in byte[] data, ICallback callback);

    void enterPrinterBuffer(boolean clean);
    void exitPrinterBufferWithCallback(boolean commit, ICallback callback);
}

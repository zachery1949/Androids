# 文件说明

### jdkstudy.java
>  涉及内容说明
1. String "==" 的使用
2. length 、length() 、size()的区别。




***



``
NAL_IDR_W_RADL(19)、NAL_IDR_N_LP(20)、NAL_CRA_NUT(21)区别？为什么拉流rtsp得到的h265的type是20或者21,而不是19
NAL_IDR_W_RADL(19)、NAL_IDR_N_LP(20)、NAL_CRA_NUT(21)和是H.265/HEVC（High Efficiency Video Coding）中的不同类型的NAL单元。
1. NAL_IDR_N_LP（Non-Reference Picture）：这是一个非参考帧的IDR帧（Instantaneous Decoder Refresh）。IDR帧是视频序列中的关键帧，它可以作为解码器的参考点，其他帧可以通过解码IDR帧来进行预测。NAL_IDR_N_LP是一种非参考帧，意味着它不依赖于其他帧进行解码。
2. NAL_CRA_NUT（Clean Random Access）：这也是一个关键帧类型，但它是一种干净的随机访问帧。与IDR帧不同，CRA帧可以作为解码器的参考点，但它不会引入任何错误或失真。这意味着在解码CRA帧之前，不需要解码其他帧。
3. NAL_IDR_W_RADL（Instantaneous Decoder Refresh with Random Access Decoding Leading）：这是一种带有随机访问解码前导的IDR帧。它类似于NAL_IDR_N_LP，但在解码IDR帧之前，需要解码一些前导帧。
至于为什么拉流RTSP得到的H.265的类型是20或21而不是19，这可能是由于不同的编码器或解码器实现的差异。H.265标准定义了一系列的NAL单元类型，其中类型19表示IDR帧。然而，实际的编码器或解码器可能会选择使用其他类型的NAL单元来表示IDR帧，这取决于具体的实现和配置。因此，你可能会在拉流RTSP时看到类型20或21来表示IDR帧。这并不违反H.265标准，只是一种不同的实现选择。
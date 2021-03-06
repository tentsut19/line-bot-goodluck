package com.cabsat.linebot;

import com.cabsat.linebot.client.LineDataClient;
import com.cabsat.linebot.client.request.*;
import com.cabsat.linebot.client.response.*;
import com.cabsat.linebot.exception.CustomException;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.io.ByteStreams;
import com.cabsat.linebot.client.EcommerceClient;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.client.MessageContentResponse;
import com.linecorp.bot.model.ReplyMessage;
import com.linecorp.bot.model.event.Event;
import com.linecorp.bot.model.event.FollowEvent;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.ImageMessageContent;
import com.linecorp.bot.model.event.message.LocationMessageContent;
import com.linecorp.bot.model.event.message.StickerMessageContent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.event.source.Source;
import com.linecorp.bot.model.message.*;
import com.linecorp.bot.model.response.BotApiResponse;
import com.linecorp.bot.spring.boot.annotation.EventMapping;
import com.linecorp.bot.spring.boot.annotation.LineMessageHandler;

import lombok.NonNull;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.cabsat.linebot.constant.ResponseConstant.INVALID_REQUEST;
import static com.cabsat.linebot.constant.ResponseConstant.NOT_FOUND;

@Slf4j
@LineMessageHandler
public class LineBotController {
    @Autowired
    private LineMessagingClient lineMessagingClient;
    @Autowired
    private LineDataClient lineDataClient;
    @Autowired
    private EcommerceClient ecommerceClient;

    public String listFolderStructure(String replyToken, String command) throws Exception {
        Session session = null;
        ChannelExec channel = null;
        String username = "root";
        String password = "F9Qe422jqr";
        String host = "203.150.140.31";
        int port = 2225;
        try {
            session = new JSch().getSession(username, host, port);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();

            channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand(command);
            ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
            channel.setOutputStream(responseStream);
            channel.connect();

            while (channel.isConnected()) {
                Thread.sleep(100);
            }

            String responseString = new String(responseStream.toByteArray());
            log.info(responseString);
            this.replyText(replyToken, responseString);
            return responseString;
        } finally {
            if (session != null) {
                session.disconnect();
            }
            if (channel != null) {
                channel.disconnect();
            }
        }
    }

    @EventMapping
    public void handleFollowEvent(FollowEvent event) throws Exception {
        log.info("========== handleFollowEvent ==========");
        log.info("event : {}",event);
    }

    @EventMapping
    public void handleTextMessage(MessageEvent<TextMessageContent> event) throws Exception {
        String userId = event.getSource().getUserId();
        String senderId = event.getSource().getSenderId();
        log.info(">>>>>>>>>>>>>>>>>>>");
        log.info("event : {}",event);
        log.info("userId : {}",userId);
        log.info("senderId : {}",senderId);

        TextMessageContent message = event.getMessage();
        handleText(event.getReplyToken(), event, message);
//        listFolderStructure(event.getReplyToken(),message.getText());
    }

    @EventMapping
    public void handleStickerMessage(MessageEvent<StickerMessageContent> event) {
        log.info(event.toString());
        StickerMessageContent message = event.getMessage();
//        reply(event.getReplyToken(), new StickerMessage(
//                message.getPackageId(), message.getStickerId()
//        ));
    }

    @EventMapping
    public void handleLocationMessage(MessageEvent<LocationMessageContent> event) {
        log.info(event.toString());
        LocationMessageContent message = event.getMessage();
        reply(event.getReplyToken(), new LocationMessage(
                (message.getTitle() == null) ? "Location replied" : message.getTitle(),
                message.getAddress(),
                message.getLatitude(),
                message.getLongitude()
        ));
    }

    @EventMapping
    public void handleImageMessage(MessageEvent<ImageMessageContent> event) {
        log.info(event.toString());
        ImageMessageContent content = event.getMessage();
        String replyToken = event.getReplyToken();

        try {
            byte[] response = lineDataClient.getContent(content.getId());
            Files.write(Paths.get(content.getId()+".jpg"), response);
            log.info("Paths : {}", Paths.get(content.getId()+".jpg"));
            UploadRequest uploadRequest = new UploadRequest();
            Path path = Paths.get(content.getId()+".jpg");
            uploadRequest.setFile(path.toFile());
            uploadRequest.setType("order");

            ResponseEntity<FileManagerResponse> responseResponseEntity = ecommerceClient.uploadImage(response,content.getId()+".jpg","order");
            if(responseResponseEntity.getStatusCode().is2xxSuccessful()) {
                FileManagerResponse fileManagerResponse = responseResponseEntity.getBody();
                log.info("CustomerResponse : {}", fileManagerResponse);
                String responseText = "??????????????????????????????????????????????????????";
                if(fileManagerResponse != null && fileManagerResponse.getData() != null){
                    responseText += "\n??????????????????????????????????????????????????? : "+fileManagerResponse.getData().getOrderCode();
                }
                this.replyText(replyToken, responseText);
            }else{
                log.error("CustomerResponse : {}", responseResponseEntity.getBody());
            }

            File deleteFile = new File(content.getId()+".jpg");
            if (deleteFile.delete()) {
                log.info("Deleted the file: " + deleteFile.getName());
            } else {
                log.info("Failed to delete the file.");
            }

//            log.info("response : {}", response);
//            DownloadedContent jpg = saveContent("jpg", response);
//            DownloadedContent previewImage = createTempFile("jpg");
//
//            system("convert", "-resize", "240x",
//                    jpg.path.toString(),
//                    previewImage.path.toString());
//
//            reply(replyToken, new ImageMessage(jpg.getUri(), previewImage.getUri()));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static File convert(MultipartFile file) throws IOException {
        File convFile = new File(file.getOriginalFilename());
        convFile.createNewFile();
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }

    private void handleText(String replyToken, Event event, TextMessageContent content) {
        String text = content.getText();
        String userId = event.getSource().getUserId();
        String senderId = event.getSource().getSenderId();
        log.info("Got text message from replyToken : {}, text : {}, userId : {}, senderId : {}", replyToken, text, userId, senderId);
        // test bot = C88b06f13b62e6112531a4b2a027cb886
        // test bot 02 = C8b81f6fa6c046f48ac65ac2d6b81ca57
        try {
            String message = "";
            boolean isCancel = text.contains("cancel");
            boolean isSummary = text.contains("?????????????????????");
            boolean isRegister = text.contains("register");
            if(isSummary){
                String startDate = "";
                String endDate = "";
//                String date = "????????????????????? 01/12/2019-02/12/2019";
//                String regex = "^(.*?)(1[0-2]|0[1-9])/(3[01]|[12][0-9]|0[1-9])/[0-9]{4}-(1[0-2]|0[1-9])/(3[01]|[12][0-9]|0[1-9])/[0-9]{4}$";
                String regex = "^(.*?)(1[0-2]|0[1-9])/(3[01]|[12][0-9]|0[1-9])/[0-9]{4}$";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(text);
                boolean bool = matcher.matches();
                if(bool){
                    matcher = pattern.matcher(text);
                    if (matcher.find())
                    {
                        text = text.replaceAll(matcher.group(1),"");
                    }
                    OrderSummaryRequest request = new OrderSummaryRequest();
                    startDate = text;
                    endDate = text;
                    log.info("startDate {}, endDate : {}", startDate, endDate);
                    request.setStartDate(startDate);
                    request.setEndDate(endDate);
                    message = summary(request);
                }else{
                    OrderSummaryRequest request = new OrderSummaryRequest();
                    LocalDate date = LocalDate.now();
                    startDate = date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                    endDate = date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                    log.info("startDate {}, endDate : {}", startDate, endDate);
                    request.setStartDate(startDate);
                    request.setEndDate(endDate);
                    message = summary(request);
                }
            }else if(isCancel){
                message = deleteOrder(text,userId,senderId);
            }else if(isRegister){
                String[] textRegister = text.trim().split("register:");
                if(textRegister.length > 1){
                    LineProfileResponse response = null;
                    try {
                        response = lineDataClient.getGroupChatSummaryEndpoint(senderId);
                    }catch (Exception e){
                        log.error(e.getMessage(), e);
                        message = "Register fail.";
                    }
                    log.info("getGroupChatSummaryEndpoint : {}", response);
                    if(response != null){
                        String taxId = textRegister[textRegister.length-1];
                        RegisterLineGroupRequest request = RegisterLineGroupRequest.builder()
                                .groupId(response.getGroupId())
                                .groupName(response.getGroupName())
                                .pictureUrl(response.getPictureUrl())
                                .build();
                        ResponseEntity<RegisterLineGroupResponse> responseResponseEntity = ecommerceClient.registerByTaxId(taxId,request);
                        if (responseResponseEntity.getStatusCode().is2xxSuccessful()) {
                            log.info("RegisterLineGroupResponse : {}", responseResponseEntity.getBody());
                            message = "Register success.";
                        }else{
                            message = "Register fail.";
                        }
                    }
                }else{
                    message = "??????????????????????????? Register ??????????????????????????????\n??????????????????????????? Register\nregister:???????????????????????????????????????????????????\n????????????????????????\nregister:1234567890123";
                }
            }else{
                message = createOrder(text,userId,senderId);
            }
            this.replyText(replyToken, message);
        }catch (CustomException e){
            if(e.getErrorCode().equalsIgnoreCase("not_register")){
                String message = "?????????????????????????????? Register!!!\n??????????????????????????? Register\nregister:???????????????????????????????????????????????????\n????????????????????????\nregister:1234567890123";
                this.replyText(replyToken, "??????????????????????????????????????????" +
                        "\n"+message);
            }else{
                this.replyText(replyToken, "??????????????????????????????????????????" +
                        "\n"+e.getMessage());
            }
        }catch (Exception e){
            e.printStackTrace();
            this.replyText(replyToken, "??????????????????????????????????????????");
        }
    }

    public String summary(OrderSummaryRequest request) throws CustomException {
        String message = "?????????????????????????????????";
        if(request != null){
            String startDate = request.getStartDate();
            if(!StringUtils.isEmpty(startDate)){
                LocalDate date = LocalDate.parse(startDate, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                if(date != null){
                    String startDateYmd = date.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
                    request.setStartDateYmd(startDateYmd);
                }
            }
            String endDate = request.getEndDate();
            if(!StringUtils.isEmpty(endDate)){
                LocalDate date = LocalDate.parse(endDate, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                if(date != null){
                    String endDateYmd = date.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
                    request.setEndDateYmd(endDateYmd);
                }
            }
        }
        ResponseEntity<List<OrderSummaryResponse>> responseResponseEntity = ecommerceClient.summary(request);
        if (responseResponseEntity.getStatusCode().is2xxSuccessful()) {
            List<OrderSummaryResponse> orderSummaryResponseList = responseResponseEntity.getBody();
            if(!CollectionUtils.isEmpty(orderSummaryResponseList)){
                message = "";
                List<OrderSummaryModel> orderSummaryModelList = convertToOrderSummaryModel(orderSummaryResponseList);
                for(OrderSummaryModel orderSummaryModel:orderSummaryModelList){
                    message += "\uD83C\uDF1F????????????????????????????????? ?????????????????? "+orderSummaryModel.getDate()+"\uD83C\uDF1F\n\n";
                    if(!CollectionUtils.isEmpty(orderSummaryModel.getOrderSummaryList())){
                        for(OrderSummary orderSummary:orderSummaryModel.getOrderSummaryList()){
                            message += orderSummary.getProductName()+"\n\n";

                            message += "\uD83D\uDCB8?????????\n\n";
                            if(!CollectionUtils.isEmpty(orderSummary.getTransferSummaryList())){
                                for(TransferSummary transferSummary:orderSummary.getTransferSummaryList()){
                                    message += ""+transferSummary.getText()+"\n";
                                }
                                message += "\n";
                            }
                            message += "??????????????????????????????: "+orderSummary.getTransferCount()+" ????????????\n";
                            message += "?????????????????????????????????: "+orderSummary.getTransferCountPrice()+" ?????????\n\n";
                            message += "\uD83D\uDCE6?????????????????????(COD)\n\n";

                            if(!CollectionUtils.isEmpty(orderSummary.getCodSummaryList())){
                                for(CodSummary codSummary:orderSummary.getCodSummaryList()){
                                    message += ""+codSummary.getText()+"\n";
                                }
                                message += "\n";
                            }
                            message += "????????? COD ?????????????????????: "+orderSummary.getCodCount()+" ????????????\n";
                            message += "?????????????????????????????????: "+orderSummary.getCodCountPrice()+" ????????????\n\n";

                            message += "????????????????????????????????? "+orderSummary.getOrderSummary()+" ???????????????\n";
                            message += "???????????????????????? "+orderSummary.getProductSummary()+" ??????????????????\n";
                            message += "?????????????????????????????????????????????????????? "+orderSummary.getTotalSales()+" ???????????????\n";
                            message += "==================================\n";
                        }
                    }
                    message += "????????????????????????????????????????????????????????? "+orderSummaryModel.getSummaryTotalSales()+" ??????????????????";
                }
            }
        } else {
            throw new CustomException(INVALID_REQUEST,"");
        }
        return message;
    }

    public List<OrderSummaryModel> convertToOrderSummaryModel(List<OrderSummaryResponse> orderSummaryResponseList) throws CustomException {
        DecimalFormat formatter = new DecimalFormat("#,###");
        List<OrderSummaryModel> orderSummaryModelList = new ArrayList<>();
        if(!CollectionUtils.isEmpty(orderSummaryResponseList)){
            List<OrderSummary> orderSummaryList = new ArrayList<>();
            OrderSummary orderSummary = new OrderSummary();
            String productName = orderSummaryResponseList.get(0).getProductDraftName();
            String productDraftName = orderSummaryResponseList.get(0).getProductDraftName();
            OrderSummaryModel orderSummaryModel = new OrderSummaryModel();
            Map<String,Integer> mapTransferSummary = new HashMap<>();
            List<TransferSummary> transferSummaryList = new ArrayList<>();
            Map<String,Integer> mapCodSummary = new HashMap<>();
            List<CodSummary> codSummaryList = new ArrayList<>();
            int summaryTotalSales = 0;
            int i = 0;
            for(OrderSummaryResponse orderSummaryResponse:orderSummaryResponseList) {
                String regex = "\\d+.";
                String[] productNames = productName.split(regex);
                if(productNames.length > 0){
                    productName = productNames[0];
                }
                regex = "\\,+.";
                productNames = productName.split(regex);
                if(productNames.length > 0){
                    productName = productNames[0];
                }
                String[] productDraftNames = orderSummaryResponse.getProductDraftName().split(regex);
                if(productDraftNames.length > 0){
                    productDraftName = productDraftNames[0];
                }

                if(!productName.equals(productDraftName)){
                    int transferCount = 0;
                    int transferCountPrice = 0;
                    int codCount = 0;
                    int codCountPrice = 0;
                    int productSummaryVal = 0;
                    for(Map.Entry<String, Integer> map:mapCodSummary.entrySet()){
                        CodSummary codSummary = new CodSummary();
                        String text = map.getKey()+"="+map.getValue();
                        codSummary.setText(text);
                        codSummaryList.add(codSummary);
                        codCount += map.getValue();
                        String key = map.getKey();
                        String[] keys = key.split("/");
                        codCountPrice += Integer.parseInt(keys[0]) * map.getValue();
                        productSummaryVal += Integer.parseInt(keys[1]) * map.getValue();
                    }
                    for(Map.Entry<String, Integer> map:mapTransferSummary.entrySet()){
                        TransferSummary transferSummary = new TransferSummary();
                        String text = map.getKey()+"="+map.getValue();
                        transferSummary.setText(text);
                        transferSummaryList.add(transferSummary);
                        transferCount += map.getValue();
                        String key = map.getKey();
                        String[] keys = key.split("/");
                        transferCountPrice += Integer.parseInt(keys[0]) * map.getValue();
                        productSummaryVal += Integer.parseInt(keys[1]) * map.getValue();
                    }
                    int orderSummaryVal = transferCount+codCount;
                    int totalSales = transferCountPrice+codCountPrice;
                    orderSummary.setTransferCount(transferCount+"");
                    orderSummary.setTransferCountPrice(formatter.format(transferCountPrice));
                    orderSummary.setCodCount(codCount+"");
                    orderSummary.setCodCountPrice(formatter.format(codCountPrice));
                    orderSummary.setOrderSummary(orderSummaryVal+"");
                    orderSummary.setProductSummary(productSummaryVal+"");
                    orderSummary.setTransferSummaryList(transferSummaryList);
                    orderSummary.setCodSummaryList(codSummaryList);
                    orderSummary.setTotalSales(formatter.format(totalSales));
                    orderSummaryList.add(orderSummary);
                    summaryTotalSales += totalSales;
                    mapTransferSummary = new HashMap<>();
                    mapCodSummary = new HashMap<>();
                    transferSummaryList = new ArrayList<>();
                    codSummaryList = new ArrayList<>();
                    orderSummary = new OrderSummary();
                    productName = orderSummaryResponse.getProductDraftName();
                    productNames = productName.split(regex);
                    if (productNames.length > 0) {
                        productName = productNames[0];
                    }
                    regex = "\\,+.";
                    productNames = productName.split(regex);
                    if(productNames.length > 0){
                        productName = productNames[0];
                    }
                }

                if("cod".equalsIgnoreCase(orderSummaryResponse.getPaymentChannel())){
                    String key = Float.valueOf(orderSummaryResponse.getTotalAmount()).intValue()+"/"+orderSummaryResponse.getQuantity();
                    if(mapCodSummary.get(key) == null){
                        mapCodSummary.put(key,1);
                    }else{
                        Integer val = mapCodSummary.get(key);
                        mapCodSummary.put(key,++val);
                    }
                }else if("?????????".equalsIgnoreCase(orderSummaryResponse.getPaymentChannel())){
                    String key = Float.valueOf(orderSummaryResponse.getTotalAmount()).intValue()+"/"+orderSummaryResponse.getQuantity();
                    if(mapTransferSummary.get(key) == null){
                        mapTransferSummary.put(key,1);
                    }else{
                        Integer val = mapTransferSummary.get(key);
                        mapTransferSummary.put(key,++val);
                    }
                }

                orderSummary.setProductName(productName);

                if((orderSummaryResponseList.size()-1) == i){
                    int transferCount = 0;
                    int transferCountPrice = 0;
                    int codCount = 0;
                    int codCountPrice = 0;
                    int productSummaryVal = 0;
                    for(Map.Entry<String, Integer> map:mapCodSummary.entrySet()){
                        CodSummary codSummary = new CodSummary();
                        String text = map.getKey()+"="+map.getValue();
                        codSummary.setText(text);
                        codSummaryList.add(codSummary);
                        codCount += map.getValue();
                        String key = map.getKey();
                        String[] keys = key.split("/");
                        codCountPrice += Integer.parseInt(keys[0]) * map.getValue();
                        productSummaryVal += Integer.parseInt(keys[1]) * map.getValue();
                    }
                    for(Map.Entry<String, Integer> map:mapTransferSummary.entrySet()){
                        TransferSummary transferSummary = new TransferSummary();
                        String text = map.getKey()+"="+map.getValue();
                        transferSummary.setText(text);
                        transferSummaryList.add(transferSummary);
                        transferCount += map.getValue();
                        String key = map.getKey();
                        String[] keys = key.split("/");
                        transferCountPrice += Integer.parseInt(keys[0]) * map.getValue();
                        productSummaryVal += Integer.parseInt(keys[1]) * map.getValue();
                    }
                    int orderSummaryVal = transferCount+codCount;
                    int totalSales = transferCountPrice+codCountPrice;
                    orderSummary.setTransferCount(transferCount+"");
                    orderSummary.setTransferCountPrice(formatter.format(transferCountPrice));
                    orderSummary.setCodCount(codCount+"");
                    orderSummary.setCodCountPrice(formatter.format(codCountPrice));
                    orderSummary.setOrderSummary(orderSummaryVal+"");
                    orderSummary.setProductSummary(productSummaryVal+"");
                    orderSummary.setTransferSummaryList(transferSummaryList);
                    orderSummary.setCodSummaryList(codSummaryList);
                    orderSummary.setTotalSales(formatter.format(totalSales));
                    orderSummaryList.add(orderSummary);
                    summaryTotalSales += totalSales;
                    orderSummaryModel.setDate(orderSummaryResponse.getCreateDate());
                    orderSummaryModel.setOrderSummaryList(orderSummaryList);
                    orderSummaryModel.setSummaryTotalSales(formatter.format(summaryTotalSales));
                    orderSummaryModelList.add(orderSummaryModel);
                }
                i++;
            }
        }
        return orderSummaryModelList;
    }

    public String deleteOrder(String text, String userId, String senderId) throws CustomException {
        String message = "";
        String orderCode = "";
        log.info("deleteOrder {} : {} : {}", text, userId, senderId);
        String[] orderCodes = text.trim().split("-");
        if(orderCodes.length > 0){
            orderCode = orderCodes[0];
        }
        if(StringUtils.isEmpty(orderCode)){
            throw new CustomException(INVALID_REQUEST,"??????????????????????????????????????????????????????????????????");
        }
        ResponseEntity<CustomerResponse> responseResponseEntity = ecommerceClient.deleteOrder(orderCode);
        if (responseResponseEntity.getStatusCode().is2xxSuccessful()) {
            CustomerResponse customerResponse = responseResponseEntity.getBody();
            String code = customerResponse.getCode();
            if (code.equalsIgnoreCase("success")) {
                log.info("CustomerResponse : {}", customerResponse);
                String productSettingQuantity = "";
                if (!CollectionUtils.isEmpty(customerResponse.getData().getProductSettingQuantity())) {
                    productSettingQuantity = "\n???????????????????????????????????????";
                    int i = 1;
                    for (ProductSettingQuantity productSetting : customerResponse.getData().getProductSettingQuantity()) {
                        productSettingQuantity += "\n" + (i++) + ". ?????????????????? : " + productSetting.getProductName() + " ??????????????? : " + productSetting.getQuantity();
                    }
                }
                message = "???????????????????????????????????????????????????????????????????????????" +
                        "\n??????????????????????????????????????????????????? : "+orderCode +
                        productSettingQuantity;
            } else {
                message = "???????????????????????????????????????????????????????????????????????????" +
                        "\n??????????????????????????????????????????????????? : "+orderCode;
            }
        } else {
            CustomerResponse customerResponse = responseResponseEntity.getBody();
            log.info("Error CustomerResponse : {}", customerResponse);
            String errorCode = INVALID_REQUEST;
            message = "??????????????????????????????????????????????????? : " + orderCode;
            if(customerResponse != null && customerResponse.getData() != null) {
                String errorMessage = "";
                if(!StringUtils.isEmpty(customerResponse.getData().getErrorMessage())){
                    errorMessage = customerResponse.getData().getErrorMessage();
                }
                message = errorMessage +
                        "\n??????????????????????????????????????????????????? : " + orderCode;
                errorCode = customerResponse.getCode();
            }
            throw new CustomException(errorCode,message);
        }
        return message;
    }

    public String createOrder(String text, String userId, String senderId) throws CustomException {
        String message = "";
        CustomerRequest customerRequest = createCustomerRequest_v1(text,senderId);
        LineProfileResponse response = null;
        customerRequest.setLineUserId(userId);
        customerRequest.setGroupId(senderId);
        try {
            response = lineDataClient.getProfileGroupEndpoint(senderId,userId);
        }catch (Exception e){
            log.error(e.getMessage(), e);
        }
        log.info("getProfileGroupEndpoint : {}", response);
        if(response != null){
            customerRequest.setDisplayName(response.getDisplayName());
            customerRequest.setPictureUrl(response.getPictureUrl());
        }
        log.info("CustomerRequest : {}", customerRequest);
        ResponseEntity<CustomerResponse> responseResponseEntity = ecommerceClient.createOrderCustomer(customerRequest);
        if (responseResponseEntity.getStatusCode().is2xxSuccessful()) {
            String code = "";
            CustomerResponse customerResponse = responseResponseEntity.getBody();
            if(customerResponse != null){
                code = customerResponse.getCode();
            }
            if (code.equalsIgnoreCase("success")) {
                log.info("CustomerResponse : {}", customerResponse);
                String productSettingQuantity = "\n????????????????????????????????????????????????????????????";
                if (!CollectionUtils.isEmpty(customerResponse.getData().getProductSettingQuantity())) {
                    productSettingQuantity = "\n???????????????????????????????????????";
                    int i = 1;
                    for (ProductSettingQuantity productSetting : customerResponse.getData().getProductSettingQuantity()) {
                        productSettingQuantity += "\n" + (i++) + ". ?????????????????? : " + productSetting.getProductName() + " ??????????????? : " + productSetting.getQuantity();
                        if(!StringUtils.isEmpty(productSetting.getPurchaseRequisitionQuantity())){
                            productSettingQuantity += "\n????????????????????????????????????????????????????????? : "+productSetting.getPurchaseRequisitionQuantity();
                        }
                    }
                }

                message = "???????????????????????????????????????????????????????????????" +
                        "\n??????????????????????????????????????????????????? : " + customerResponse.getData().getCode() +
                        productSettingQuantity +
                        "\n?????????????????? : " + customerRequest.getName();
            } else {
                message = "???????????????????????????????????????????????????????????????" +
                        "\n?????????????????? : " + customerRequest.getName();
            }
        } else {
            CustomerResponse customerResponse = responseResponseEntity.getBody();
            log.info("Error CustomerResponse : {}", customerResponse);
            String errorCode = INVALID_REQUEST;
            message = "?????????????????? : " + customerRequest.getName();
            if(customerResponse != null && customerResponse.getData() != null) {
                String errorMessage = "";
                if(!StringUtils.isEmpty(customerResponse.getData().getErrorMessage())){
                    errorMessage = customerResponse.getData().getErrorMessage();
                }
                message = errorMessage +
                        "\n?????????????????? : " + customerRequest.getName();
                errorCode = customerResponse.getCode();
            }
            throw new CustomException(errorCode,message);
        }
        return message;
    }

    public CustomerRequest createCustomerRequest_v1(String text,String groupId) throws CustomException {
        String REGEX = "\n";
        String textError = "";
        List<OrderRequest> orderList = new ArrayList<>();
        CustomerRequest customerRequest = new CustomerRequest();
        try {
            if(StringUtils.isEmpty(text)){
                return null;
            }

            String[] textResult = text.split(REGEX);
            log.info("String length : "+textResult.length);
            if(textResult.length < 6){
                throw new NullPointerException();
            }
            List<ProductSettingResponse> productSettingList = new ArrayList<>();
            ResponseEntity<List<ProductSettingResponse>> productSettingResponse = ecommerceClient.getProductSetting(groupId);
            if (productSettingResponse.getStatusCode().is2xxSuccessful()) {
                productSettingList = productSettingResponse.getBody();
            }

            textError = text.replaceAll("\n","#\n");

            int i = 0;
            for(String s:textResult){
                log.info("text : "+s);
                if(i == 0){ // name or order_code
                    customerNameOrOrderCode(s,customerRequest);
                }else if(i == 1){ // address
                    customerAddress(s,customerRequest);
                }else if(i == 2) { // phone_number
                    customerPhoneNumber(s,customerRequest);
                }else if(i == 3) { // social_name
                    customerSocialName(s,customerRequest);
                }else if(i == 4) { // payment_channel and price
                    paymentChannelAndPrice(s,customerRequest);
                }else { // order
                    orderList.add(order(s,productSettingList));
                }
                i++;
            }
            customerRequest.setOrderList(orderList);

        }catch (CustomException e){
            if(e.getErrorCode().equals(NOT_FOUND)){
                throw new CustomException(NOT_FOUND,e.getMessage());
            }else{
                throw new CustomException(INVALID_REQUEST,textError+
                        "\n====================\n????????????????????????????????? # ???????????????????????????????????????????????????????????????????????????????????????????????????????????????\n====================\n"+e.getMessage());
            }
        }
        return customerRequest;
    }

    public void customerNameOrOrderCode(String text, CustomerRequest customerRequest) throws CustomException {
        String regex = "\\d+.";
        String[] conditions = text.trim().split("\\+");
        if(conditions.length > 1){
            customerRequest.setCondition(conditions[0]);
            text = conditions[1];
        }
        String[] customerNameOrOrderCode = text.trim().split("-");
        customerRequest.setRecipientName(text);
        if(customerNameOrOrderCode.length == 1){
            customerRequest.setName(customerNameOrOrderCode[0].replaceAll(regex,"").trim());
        }else if(customerNameOrOrderCode.length == 2){
            customerRequest.setOrderCode(customerNameOrOrderCode[0]);
            customerRequest.setName(customerNameOrOrderCode[1].replaceAll(regex,"").trim());
        }else{
            throw new CustomException(INVALID_REQUEST,"???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????? - ????????????");
        }
    }

    public void customerAddress(String text, CustomerRequest customerRequest) {
        customerRequest.setAddress(text.trim());
    }

    public void customerPhoneNumber(String text, CustomerRequest customerRequest) throws CustomException {
        // validate
        validatePhoneNumber(text);
        customerRequest.setPhoneNumber(text.trim());
    }

    public void validatePhoneNumber(String text) throws CustomException {
        String[] phoneNumbers = text.trim().split("-");
        for(String phoneNumber:phoneNumbers){
            phoneNumber = phoneNumber.replaceAll("\u200B", "");
            if(phoneNumber.length() < 10){
                throw new CustomException(INVALID_REQUEST,"???????????????????????? : "+phoneNumber+" ?????????????????? 10 ????????????");
            }
            if(!isNumeric(phoneNumber)){
                throw new CustomException(INVALID_REQUEST,"???????????????????????? : ["+phoneNumber+"] ??????????????????????????????");
            }
        }
    }

    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    public void customerSocialName(String text, CustomerRequest customerRequest) {
        customerRequest.setSocialName(text.trim());
    }

    public void paymentChannelAndPrice(String text, CustomerRequest customerRequest) throws CustomException {
        String[] paymentChannelAndPrice = text.split("-");
        if(paymentChannelAndPrice.length < 2){
            throw new CustomException(INVALID_REQUEST,text+"\n????????????????????????????????????????????????????????????????????????????????????????????????");
        }
        if(!StringUtils.isEmpty(paymentChannelAndPrice[1])){
            try {
                Integer.valueOf(paymentChannelAndPrice[1].trim());
            }catch (Exception e){
                throw new CustomException(INVALID_REQUEST,text+"\n?????????????????????????????????????????????????????????????????????????????????????????????");
            }
        }
        customerRequest.setPaymentChannel(paymentChannelAndPrice[0].trim());
        customerRequest.setPrice(paymentChannelAndPrice[1].trim());
    }

    public OrderRequest order(String text, List<ProductSettingResponse> productSettingList) throws CustomException {
        OrderRequest orderRequest = new OrderRequest();
        String[] orders = text.trim().split(" ");
        String name = "";
        if(orders.length > 0){
            log.info("orders : {}",orders);
            boolean isProduct = false;
            boolean formatProductName = true;
            boolean formatProductQuantity = true;
            boolean formatProductColor = true;
            boolean formatProductSize = true;
            if(!CollectionUtils.isEmpty(productSettingList)){
                name = orders[0];
                for(ProductSettingResponse response:productSettingList){
                    log.info("response.getName() : {} == name : {}",response.getName(),name);
                    if(response.getName().equalsIgnoreCase(name)){
                        formatProductName = response.getFormatProductName();
                        formatProductQuantity = response.getFormatProductQuantity();
                        formatProductColor = response.getFormatProductColor();
                        formatProductSize = response.getFormatProductSize();
                        isProduct = true;
                    }
                }
            }
            if(!isProduct){
                String errorMessage = "??????????????????:"+name+"\n?????????????????????????????????";
                throw new CustomException(NOT_FOUND,errorMessage);
            }
            log.info("name : {}",name);
            log.info("formatProductName : "+formatProductName);
            log.info("formatProductQuantity : "+formatProductQuantity);
            log.info("formatProductColor : "+formatProductColor);
            log.info("formatProductSize : "+formatProductSize);
            String nameFormatProduct = "";
            int countIndex = 0;
            if(formatProductName){
                countIndex++;
                nameFormatProduct = "??????????????????????????????";
            }
            if(formatProductQuantity){
                countIndex++;
                nameFormatProduct += " ???????????????";
            }
            if(formatProductColor){
                countIndex++;
                nameFormatProduct += " ??????";
            }
            if(formatProductSize){
                countIndex++;
                nameFormatProduct += " ????????????";
            }
            log.info("orders.length : {}, countIndex : {} ",orders.length,countIndex);
            // check index
            if(orders.length > countIndex){
                String errorMessage = "?????????????????????????????????????????????????????????????????????????????????????????????\n????????????????????????????????????:"+name+"\n?????????????????????????????????????????????????????????????????????????????????????????????????????????\n????????????????????????????????????????????????\n"+nameFormatProduct;
                throw new CustomException(INVALID_REQUEST,errorMessage);
            }else if(orders.length < countIndex){
                String errorMessage = "?????????????????????????????????????????????????????????????????????????????????????????????\n????????????????????????????????????:"+name+"\n?????????????????????????????????????????????????????????????????????????????????????????????????????????\n????????????????????????????????????????????????\n"+nameFormatProduct;
                throw new CustomException(INVALID_REQUEST,errorMessage);
            }

            int i = 0;
            for(String order:orders){
                if(i == 0){
                    orderRequest.setProductName(order);
                }else if(i == 1){
                    orderRequest.setQuantity(order);
                }else if(i == 2){
                    orderRequest.setColor(order);
                }else if(i == 3){
                    orderRequest.setSize(order);
                }
                i++;
            }
        }
        return orderRequest;
    }

    public CustomerRequest createCustomerRequest(String text) {
        CustomerRequest customerRequest = new CustomerRequest();
        if(StringUtils.isEmpty(text)){
            return null;
        }

        Pattern MY_PATTERN = Pattern.compile("???????????????:(.*?);");
        Matcher m = MY_PATTERN.matcher(text);
        while (m.find()) {
            String no = m.group(1);
            customerRequest.setNo(no);
        }
        MY_PATTERN = Pattern.compile("??????????????????????????????????????????:(.*?);");
        m = MY_PATTERN.matcher(text);
        while (m.find()) {
            String orderCode = m.group(1);
            customerRequest.setOrderCode(orderCode);
        }
        MY_PATTERN = Pattern.compile("????????????:(.*?);");
        m = MY_PATTERN.matcher(text);
        while (m.find()) {
            String name = m.group(1);
            customerRequest.setName(name);
        }
        MY_PATTERN = Pattern.compile("?????????????????????:(.*?);");
        m = MY_PATTERN.matcher(text);
        while (m.find()) {
            String address = m.group(1);
            customerRequest.setAddress(address);
        }
        MY_PATTERN = Pattern.compile("???????????????:(.*?);");
        m = MY_PATTERN.matcher(text);
        while (m.find()) {
            String phoneNumber = m.group(1);
            customerRequest.setPhoneNumber(phoneNumber);
        }
        MY_PATTERN = Pattern.compile("?????????????????????:(.*?);");
        m = MY_PATTERN.matcher(text);
        while (m.find()) {
            String socialName = m.group(1);
            customerRequest.setSocialName(socialName);
        }
        MY_PATTERN = Pattern.compile("????????????:(.*?);");
        m = MY_PATTERN.matcher(text);
        while (m.find()) {
            String paymentChannel = m.group(1);
            customerRequest.setPaymentChannel(paymentChannel);
        }
        MY_PATTERN = Pattern.compile("????????????:(.*?);");
        m = MY_PATTERN.matcher(text);
        while (m.find()) {
            String price = m.group(1);
            customerRequest.setPrice(price);
        }
        List<OrderRequest> orderList = new ArrayList<>();
        MY_PATTERN = Pattern.compile("\\((.*?)\\)");
        Matcher m_order = MY_PATTERN.matcher(text);
        while (m_order.find()) {
            OrderRequest orderRequest = new OrderRequest();
            String order = m_order.group(1);
            MY_PATTERN = Pattern.compile("????????????:(.*?);");
            m = MY_PATTERN.matcher(order);
            while (m.find()) {
                String size = m.group(1);
                orderRequest.setSize(size);
            }
            MY_PATTERN = Pattern.compile("??????????????????????????????:(.*?);");
            m = MY_PATTERN.matcher(order);
            while (m.find()) {
                String productCode = m.group(1);
                orderRequest.setProductCode(productCode);
            }
            MY_PATTERN = Pattern.compile("??????????????????:(.*?);");
            m = MY_PATTERN.matcher(order);
            while (m.find()) {
                String productName = m.group(1);
                orderRequest.setProductName(productName);
            }
            MY_PATTERN = Pattern.compile("??????:(.*?);");
            m = MY_PATTERN.matcher(order);
            while (m.find()) {
                String color = m.group(1);
                orderRequest.setColor(color);
            }
            MY_PATTERN = Pattern.compile("???????????????:(.*?);");
            m = MY_PATTERN.matcher(order);
            while (m.find()) {
                String quantity = m.group(1);
                orderRequest.setQuantity(quantity);
            }
            orderList.add(orderRequest);
        }
        customerRequest.setOrderList(orderList);
        return customerRequest;
    }

    private void handleTextContent(String replyToken, Event event, TextMessageContent content) {
        String text = content.getText();

        log.info("Got text message from %s : %s", replyToken, text);

        switch (text) {
            case "Profile": {
                String userId = event.getSource().getUserId();
                if(userId != null) {
                    lineMessagingClient.getProfile(userId)
                            .whenComplete((profile, throwable) -> {
                                if(throwable != null) {
                                    this.replyText(replyToken, throwable.getMessage());
                                    return;
                                }
                                this.reply(replyToken, Arrays.asList(
                                        new TextMessage("Display name: " + profile.getDisplayName()),
                                        new TextMessage("Status message: " + profile.getStatusMessage()),
                                        new TextMessage("User ID: " + profile.getUserId())
                                ));
                            });
                }
                break;
            }
            default:
                log.info("Return echo message {} : {}", replyToken, text);
                this.replyText(replyToken, text);
        }
    }

    private void handleStickerContent(String replyToken, StickerMessageContent content) {
        reply(replyToken, new StickerMessage(
                content.getPackageId(), content.getStickerId()
        ));
    }

    private void replyText(@NonNull  String replyToken, @NonNull String message) {
        if(replyToken.isEmpty()) {
            throw new IllegalArgumentException("replyToken is not empty");
        }

        if(message.length() > 5000) {
            message = message.substring(0, 5000 - 2) + "..";
        }
        this.reply(replyToken, new TextMessage(message));
    }

    private void reply(@NonNull String replyToken, @NonNull Message message) {
        reply(replyToken, Collections.singletonList(message));
    }

    private void reply(@NonNull String replyToken, @NonNull List<Message> messages) {
        try {
            BotApiResponse response = lineMessagingClient.replyMessage(
                    new ReplyMessage(replyToken, messages)
            ).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    private void system(String... args) {
        ProcessBuilder processBuilder = new ProcessBuilder(args);
        try {
            Process start = processBuilder.start();
            int i = start.waitFor();
            log.info("result: {} => {}", Arrays.toString(args), i);
        } catch (InterruptedException e) {
            log.info("Interrupted", e);
            Thread.currentThread().interrupt();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static DownloadedContent saveContent(String ext, MessageContentResponse response) {
        log.info("Content-type: {}", response);
        DownloadedContent tempFile = createTempFile(ext);
        try (OutputStream outputStream = Files.newOutputStream(tempFile.path)) {
            ByteStreams.copy(response.getStream(), outputStream);
            log.info("Save {}: {}", ext, tempFile);
            return tempFile;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static DownloadedContent createTempFile(String ext) {
        String fileName = LocalDateTime.now() + "-" + UUID.randomUUID().toString() + "." + ext;
        Path tempFile = Application.downloadedContentDir.resolve(fileName);
        tempFile.toFile().deleteOnExit();
        return new DownloadedContent(tempFile, createUri("/downloaded/" + tempFile.getFileName()));

    }

    private static String createUri(String path) {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(path).toUriString();
    }

    @Value
    public static class DownloadedContent {
        Path path;
        String uri;
    }
}

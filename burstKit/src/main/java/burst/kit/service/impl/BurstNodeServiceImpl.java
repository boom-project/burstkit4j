package burst.kit.service.impl;

import burst.kit.entity.*;
import burst.kit.entity.response.*;
import burst.kit.util.BurstKitUtils;
import burst.kit.util.SchedulerAssigner;

import burst.kit.service.BurstNodeService;
import io.reactivex.Single;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.*;

import java.util.Map;
import java.util.Set;

public final class BurstNodeServiceImpl implements BurstNodeService {

    private final SchedulerAssigner schedulerAssigner;

    private BlockchainService blockchainService;

    public BurstNodeServiceImpl(String nodeAddress, SchedulerAssigner schedulerAssigner) {
        this.schedulerAssigner = schedulerAssigner;
        buildServices(nodeAddress);
    }

    private void buildServices(String nodeAddress) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(nodeAddress)
                .addConverterFactory(GsonConverterFactory.create(BurstKitUtils.buildGson().create()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        blockchainService = retrofit.create(BlockchainService.class);
    }
    
    private <T> Single<T> assign(Single<T> source) {
        return schedulerAssigner.assignSchedulers(source.map(this::checkBrsResponse));
    }

    private <T> T checkBrsResponse(T source) throws BRSError {
        if (source instanceof BRSResponse) {
            ((BRSResponse) source).throwIfError();
        }
        return source;
    }

    @Override
    public void updateNodeAddress(String newNodeAddress) {
        buildServices(newNodeAddress);
    }

    @Override
    public Single<BlockResponse> getBlock(BurstID block) {
        return assign(blockchainService.getBlock(block.getID(), null, null, null));
    }

    @Override
    public Single<BlockResponse> getBlock(long height) {
        return assign(blockchainService.getBlock(null, String.valueOf(height), null, null));
    }

    @Override
    public Single<BlockResponse> getBlock(BurstTimestamp timestamp) {
        return assign(blockchainService.getBlock(null, null, String.valueOf(timestamp.getTimestamp()), null));
    }

    @Override
    public Single<BlockResponse> getBlock(BurstID[] includedTransactions) {
        String[] transactions = new String[includedTransactions.length];
        for(int i = 0; i < includedTransactions.length; i++) {
            transactions[i] = includedTransactions[i].getID();
        }
        return assign(blockchainService.getBlock(null, null, null, transactions));
    }

    @Override
    public Single<BlockIDResponse> getBlockId(long height) {
        return assign(blockchainService.getBlockID(String.valueOf(height)));
    }

    @Override
    public Single<BlockchainStatusResponse> getBlockchainStatus() {
        return assign(blockchainService.getBlockchainStatus());
    }

    @Override
    public Single<BlocksResponse> getBlocks(long firstIndex, long lastIndex) {
        return assign(blockchainService.getBlocks(String.valueOf(firstIndex), String.valueOf(lastIndex), null));
    }

    @Override
    public Single<ConstantsResponse> getConstants() {
        return assign(blockchainService.getConstants());
    }

    @Override
    public Single<AccountResponse> getAccount(BurstAddress accountId) {
        return assign(blockchainService.getAccount(accountId.getID()));
    }

    @Override
    public Single<AccountATsResponse> getAccountATs(BurstAddress accountId) {
        return assign(blockchainService.getAccountATs(accountId.getID()));
    }

    @Override
    public Single<AccountBlockIDsResponse> getAccountBlockIDs(BurstAddress accountId) {
        return assign(blockchainService.getAccountBlockIDs(accountId.getID(), null, null, null));
    }

    @Override
    public Single<AccountBlocksResponse> getAccountBlocks(BurstAddress accountId) {
        return assign(blockchainService.getAccountBlocks(accountId.getID(), null, null, null, null));
    }

    @Override
    public Single<AccountPublicKeyResponse> getAccountPublicKey(BurstAddress accountId) {
        return assign(blockchainService.getAccountPublicKey(accountId.getID()));
    }

    @Override
    public Single<AccountTransactionIDsResponse> getAccountTransactionIDs(BurstAddress accountId) {
        return assign(blockchainService.getAccountTransactionIDs(accountId.getID(), null, null, null, null, null, null));
    }

    @Override
    public Single<AccountTransactionsResponse> getAccountTransactions(BurstAddress accountId) {
        return assign(blockchainService.getAccountTransactions(accountId.getID(), null, null, null, null, null, null));
    }

    @Override
    public Single<AccountsWithRewardRecipientResponse> getAccountsWithRewardRecipient(BurstAddress accountId) {
        return assign(blockchainService.getAccountsWithRewardRecipient(accountId.getID()));
    }

    @Override
    public Single<ATResponse> getAt(BurstID atId) {
        return assign(blockchainService.getAt(atId.getID()));
    }

    @Override
    public Single<AtIDsResponse> getAtIds() {
        return assign(blockchainService.getAtIds());
    }

    @Override
    public Single<TransactionResponse> getTransaction(BurstID transactionId) {
        return assign(blockchainService.getTransaction(transactionId.getID(), null));
    }

    @Override
    public Single<TransactionResponse> getTransaction(byte[] fullHash) {
        return assign(blockchainService.getTransaction(null, new HexStringByteArray(fullHash).toHexString()));
    }

    @Override
    public Single<TransactionBytesResponse> getTransactionBytes(BurstID transactionId) {
        return assign(blockchainService.getTransactionBytes(transactionId.getID()));
    }

    @Override
    public Single<GenerateTransactionResponse> generateTransaction(BurstAddress recipient, byte[] senderPublicKey, BurstValue amount, BurstValue fee, int deadline) {
        return assign(blockchainService.sendMoney(recipient.getID(), null, amount.toPlanck(), null, new HexStringByteArray(senderPublicKey).toHexString(), fee.toPlanck(), deadline, null, false, null, null, null, null, null, null, null, null, null, null));
    }

    @Override
    public Single<GenerateTransactionResponse> generateTransactionWithMessage(BurstAddress recipient, byte[] senderPublicKey, BurstValue amount, BurstValue fee, int deadline, String message) {
        return assign(blockchainService.sendMoney(recipient.getID(), null, amount.toPlanck(), null, new HexStringByteArray(senderPublicKey).toHexString(), fee.toPlanck(), deadline, null, false, message, true, null, null, null, null, null, null, null, null));
    }

    @Override
    public Single<GenerateTransactionResponse> generateTransactionWithMessage(BurstAddress recipient, byte[] senderPublicKey, BurstValue amount, BurstValue fee, int deadline, byte[] message) {
        return assign(blockchainService.sendMoney(recipient.getID(), null, amount.toPlanck(), null, new HexStringByteArray(senderPublicKey).toHexString(), fee.toPlanck(), deadline, null, false, new HexStringByteArray(message).toHexString(), false, null, null, null, null, null, null, null, null));
    }

    @Override
    public Single<GenerateTransactionResponse> generateTransactionWithEncryptedMessage(BurstAddress recipient, byte[] senderPublicKey, BurstValue amount, BurstValue fee, int deadline, BurstEncryptedMessage message) {
        return assign(blockchainService.sendMoney(recipient.getID(), null, amount.toPlanck(), null, new HexStringByteArray(senderPublicKey).toHexString(), fee.toPlanck(), deadline, null, false, null, null, null, message.isText(), message.getHexStringData().toString(), message.getHexStringNonce().toString() ,null, null, null, null));
    }

    @Override
    public Single<GenerateTransactionResponse> generateTransactionWithEncryptedMessageToSelf(BurstAddress recipient, byte[] senderPublicKey, BurstValue amount, BurstValue fee, int deadline, BurstEncryptedMessage message) {
        return assign(blockchainService.sendMoney(recipient.getID(), null, amount.toPlanck(), null, new HexStringByteArray(senderPublicKey).toHexString(), fee.toPlanck(), deadline, null, false, null, null, null, null, null, null, null, message.isText(), message.getHexStringData().toString(), message.getHexStringNonce().toString()));
    }

    @Override
    public Single<SuggestFeeResponse> suggestFee() {
        return assign(blockchainService.suggestFee());
    }

    @Override
    public Single<MiningInfoResponse> getMiningInfo() {
        return assign(blockchainService.getMiningInfo());
    }

    @Override
    public Single<MyInfoResponse> getMyInfo() {
        return assign(blockchainService.getMyInfo());
    }

    @Override
    public Single<BroadcastTransactionResponse> broadcastTransaction(byte[] transactionBytes) {
        return assign(blockchainService.broadcastTransaction(new HexStringByteArray(transactionBytes).toHexString()));
    }

    @Override
    public Single<RewardRecipientResponse> getRewardRecipient(BurstAddress account) {
        return assign(blockchainService.getRewardRecipient(account.getID()));
    }

    @Override
    public Single<SubmitNonceResponse> submitNonce(String passphrase, String nonce, BurstID accountId) {
        return assign(blockchainService.submitNonce(passphrase, nonce, accountId == null ? null : accountId.getID(), ""));
    }

    @Override
    public Single<GenerateTransactionResponse> generateMultiOutTransaction(byte[] senderPublicKey, BurstValue fee, int deadline, Map<BurstAddress, BurstValue> recipients) throws IllegalArgumentException {
        StringBuilder recipientsString = new StringBuilder();
        if (recipients.size() > 64 || recipients.size() < 2) {
            throw new IllegalArgumentException("Must have 2-64 recipients, had " + recipients.size());
        }
        for (Map.Entry<BurstAddress, BurstValue> recipient : recipients.entrySet()) {
            recipientsString.append(recipient.getKey().getID()).append(":").append(recipient.getValue().toPlanck()).append(";");
        }
        recipientsString.setLength(recipientsString.length() - 1);
        return assign(blockchainService.sendMoneyMulti(null, new HexStringByteArray(senderPublicKey).toHexString(), fee.toPlanck(), String.valueOf(deadline), null, false, recipientsString.toString()));
    }

    @Override
    public Single<GenerateTransactionResponse> generateMultiOutSameTransaction(byte[] senderPublicKey, BurstValue amount, BurstValue fee, int deadline, Set<BurstAddress> recipients) throws IllegalArgumentException {
        StringBuilder recipientsString = new StringBuilder();
        if (recipients.size() > 128 || recipients.size() < 2) {
            throw new IllegalArgumentException("Must have 2-128 recipients, had " + recipients.size());
        }
        for (BurstAddress recipient : recipients) {
            recipientsString.append(recipient.getID()).append(";");
        }
        recipientsString.setLength(recipientsString.length() - 1);
        return assign(blockchainService.sendMoneyMultiSame(null, new HexStringByteArray(senderPublicKey).toHexString(), fee.toPlanck(), String.valueOf(deadline), null, false, recipientsString.toString(), amount.toPlanck()));
    }

    private interface BlockchainService {
        @GET("boom?requestType=getBlock")
        Single<BlockResponse> getBlock(@Query("block") String blockId, @Query("height") String blockHeight, @Query("timestamp") String timestamp, @Query("includeTransactions") String[] transactions); // TODO Array of transactions

        @GET("boom?requestType=getBlockId")
        Single<BlockIDResponse> getBlockID(@Query("height") String blockHeight);

        @GET("boom?requestType=getBlockchainStatus")
        Single<BlockchainStatusResponse> getBlockchainStatus();

        @GET("boom?requestType=getBlocks")
        Single<BlocksResponse> getBlocks(@Query("firstIndex") String firstIndex, @Query("lastIndex") String lastIndex, @Query("includeTransactions") String[] transactions);

        @GET("boom?requestType=getConstants")
        Single<ConstantsResponse> getConstants();

        @GET("boom?requestType=getAccount")
        Single<AccountResponse> getAccount(@Query("account") String accountId);

        @GET("boom?requestType=getAccountATs")
        Single<AccountATsResponse> getAccountATs(@Query("account") String accountId);

        @GET("boom?requestType=getAccountBlockIds")
        Single<AccountBlockIDsResponse> getAccountBlockIDs(@Query("account") String accountId, @Query("timestamp") String timestamp, @Query("firstIndex") String firstIndex, @Query("lastIndex") String lastIndex);

        @GET("boom?requestType=getAccountBlocks")
        Single<AccountBlocksResponse> getAccountBlocks(@Query("account") String accountId, @Query("timestamp") String timestamp, @Query("firstIndex") String firstIndex, @Query("lastIndex") String lastIndex, @Query("includeTransactions") String[] includedTransactions);

        @GET("boom?requestType=getAccountPublicKey")
        Single<AccountPublicKeyResponse> getAccountPublicKey(@Query("account") String accountId);

        @GET("boom?requestType=getAccountTransactionIds")
        Single<AccountTransactionIDsResponse> getAccountTransactionIDs(@Query("account") String accountId, @Query("timestamp") String timestamp, @Query("type") String type, @Query("subtype") String subtype, @Query("firstIndex") String firstIndex, @Query("lastIndex") String lastIndex, @Query("numberOfConfirmations") String numberOfConfirmations);

        @GET("boom?requestType=getAccountTransactions")
        Single<AccountTransactionsResponse> getAccountTransactions(@Query("account") String accountId, @Query("timestamp") String timestamp, @Query("type") String type, @Query("subtype") String subtype, @Query("firstIndex") String firstIndex, @Query("lastIndex") String lastIndex, @Query("numberOfConfirmations") String numberOfConfirmations);

        @GET("boom?requestType=getAccountsWithRewardRecipient")
        Single<AccountsWithRewardRecipientResponse> getAccountsWithRewardRecipient(@Query("account") String accountId);

        @GET("boom?requestType=getAT")
        Single<ATResponse> getAt(@Query("at") String atId);

        @GET("boom?requestType=getATIds")
        Single<AtIDsResponse> getAtIds();

        @GET("boom?requestType=getTransaction")
        Single<TransactionResponse> getTransaction(@Query("transaction") String transaction, @Query("fullHash") String fullHash);

        @GET("boom?requestType=getTransactionBytes")
        Single<TransactionBytesResponse> getTransactionBytes(@Query("transaction") String transaction);

        @POST("boom?requestType=sendMoney")
        Single<GenerateTransactionResponse> sendMoney(@Query("recipient") String recipient, @Query("recipientPublicKey") String recipientPublicKey, @Query("amountNQT") String amount, @Query("secretPhrase") String secretPhrase, @Query("publicKey") String publicKey, @Query("feeNQT") String fee, @Query("deadline") int deadline, @Query("referencedTransactionFullHash") String referencedTransactionFullHash, @Query("broadcast") boolean broadcast, @Query("message") String message, @Query("messageIsText") Boolean messageIsText, @Query("messageToEncrypt") String messageToEncrypt, @Query("messageToEncryptIsText") Boolean messageToEncryptIsText, @Query("encryptedMessageData") String encryptedMessageData, @Query("encryptedMessageNonce") String encryptedMessageNonce, @Query("messageToEncryptToSelf") String messageToEncryptToSelf, @Query("messageToEncryptToSelfIsText") Boolean messageToEncryptToSelfIsText, @Query("encryptedToSelfMessageData") String encryptedToSelfMessageData, @Query("encryptedToSelfMessageNonce") String encryptedToSelfMessageNonce);

        @GET("boom?requestType=suggestFee")
        Single<SuggestFeeResponse> suggestFee();

        @GET("boom?requestType=getMiningInfo")
        Single<MiningInfoResponse> getMiningInfo();

        @GET("boom?requestType=getMyInfo")
        Single<MyInfoResponse> getMyInfo();

        @POST("boom?requestType=broadcastTransaction")
        Single<BroadcastTransactionResponse> broadcastTransaction(@Query("transactionBytes") String transactionBytes);

        @GET("boom?requestType=getRewardRecipient")
        Single<RewardRecipientResponse> getRewardRecipient(@Query("account") String account);

        @POST("boom?requestType=submitNonce")
        Single<SubmitNonceResponse> submitNonce(@Query("secretPhrase") String passphrase, @Query("nonce") String nonce, @Query("accountId") String accountId, @Query("blockheight") String blockheight);

        @POST("boom?requestType=sendMoneyMulti")
        Single<GenerateTransactionResponse> sendMoneyMulti(@Query("secretPhrase") String secretPhrase, @Query("publicKey") String publicKey, @Query("feeNQT") String feeNQT, @Query("deadline") String deadline, @Query("referencedTransactionFullHash") String referencedTransactionFullHash, @Query("broadcast") boolean broadcast, @Query("recipients") String recipients);

        @POST("boom?requestType=sendMoneyMultiSame")
        Single<GenerateTransactionResponse> sendMoneyMultiSame(@Query("secretPhrase") String secretPhrase, @Query("publicKey") String publicKey, @Query("feeNQT") String feeNQT, @Query("deadline") String deadline, @Query("referencedTransactionFullHash") String referencedTransactionFullHash, @Query("broadcast") boolean broadcast, @Query("recipients") String recipients, @Query("amountNQT") String amountNQT);
    }
}

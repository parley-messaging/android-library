package nu.parley.android.data.messages;

import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import nu.parley.android.Parley;
import nu.parley.android.data.model.Message;

/**
 * An implementation of caching Parley data by using AES encryption.
 *
 * <p>
 * Based on: https://www.raywenderlich.com/778533-encryption-tutorial-for-android-getting-started
 * </p>
 */
public final class ParleyEncryptedDataSource implements ParleyDataSource {

    private static final int ENCRYPTION_KEY_LENGTH = 256;
    private static final int ENCRYPTION_IV_LENGTH = 16;

    private static final int ENCRYPTION_ITERATION_COUNT = 1324;

    private static final String ENCRYPTION_CIPHER_TRANSFORMATION = "AES/CBC/PKCS7Padding";
    private static final String ENCRYPTION_SECRET_KEY_ALGORITHM = "PBKDF2WithHmacSHA1";
    private static final String ENCRYPTION_FILE_NAME_MESSAGES = "messages";
    private static final String ENCRYPTION_FILE_NAME_INFO = "info";
    private static final String ENCRYPTION_FILE_NAME_PAGING = "paging";

    private final File cacheFileMessages;
    private final File cacheFileInfo;
    private final File cacheFilePaging;
    private final String key;

    public ParleyEncryptedDataSource(Context context, String key) {
        this.key = key;
        this.cacheFileMessages = new File(context.getExternalCacheDir() + "/" + ENCRYPTION_FILE_NAME_MESSAGES);
        this.cacheFileInfo = new File(context.getExternalCacheDir() + "/" + ENCRYPTION_FILE_NAME_INFO);
        this.cacheFilePaging = new File(context.getExternalCacheDir() + "/" + ENCRYPTION_FILE_NAME_PAGING);
    }

    private void saveToFile(File file, byte[] data, byte[] salt, byte[] iv) {
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            outputStream.write(data);
            outputStream.write(salt);
            outputStream.write(iv);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private byte[] getFromFile(File file) {
        int size = (int) file.length();
        byte[] bytes = new byte[size];
        try (FileInputStream fileInputStream = new FileInputStream(file);
             BufferedInputStream buf = new BufferedInputStream(fileInputStream)) {
            buf.read(bytes, 0, bytes.length);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }

    private byte[] encrypt(SecretKeySpec keySpec, byte[] iv, byte[] dataToEncrypt) throws InvalidAlgorithmParameterException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException {
        return getCipher(Cipher.ENCRYPT_MODE, keySpec, iv).doFinal(dataToEncrypt);
    }

    private byte[] decrypt(SecretKeySpec keySpec, byte[] iv, byte[] encrypted) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, BadPaddingException, IllegalBlockSizeException {
        return getCipher(Cipher.DECRYPT_MODE, keySpec, iv).doFinal(encrypted);
    }

    private Cipher getCipher(int cipherMode, SecretKeySpec keySpec, byte[] iv) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException {
        Cipher cipher = Cipher.getInstance(ENCRYPTION_CIPHER_TRANSFORMATION);
        cipher.init(cipherMode, keySpec, new IvParameterSpec(iv));
        return cipher;
    }

    private byte[] createSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[ENCRYPTION_KEY_LENGTH];
        random.nextBytes(salt);

        return salt;
    }

    private SecretKeySpec generateKey(String password, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        PBEKeySpec pbeKeySpec = new PBEKeySpec(password.toCharArray(), salt, ENCRYPTION_ITERATION_COUNT, ENCRYPTION_KEY_LENGTH);
        SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(ENCRYPTION_SECRET_KEY_ALGORITHM);
        byte[] keyBytes = secretKeyFactory.generateSecret(pbeKeySpec).getEncoded();
        return new SecretKeySpec(keyBytes, "AES");
    }

    private byte[] createIv() {
        SecureRandom ivRandom = new SecureRandom();
        byte[] iv = new byte[ENCRYPTION_IV_LENGTH];
        ivRandom.nextBytes(iv);
        return iv;
    }

    private void cacheMessages(List<Message> messages) {
        String messageInJson = Parley.getInstance().getNetwork().config.getJsonParser().messagesToJson(messages);
        cacheData(cacheFileMessages, messageInJson.getBytes());
    }

    private void cacheValue(String key, String value) {
        File destinationFile;
        switch (key) {
            case KEY_MESSAGE_INFO:
                destinationFile = cacheFileInfo;
                break;
            case KEY_PAGING:
                destinationFile = cacheFilePaging;
                break;
            default:
                Log.d("EncryptedDataSource", "cacheValue :: Unexpected value for key: " + key);
                return;
        }

        if (value == null) {
            destinationFile.delete();
        } else {
            cacheData(destinationFile, value.getBytes());
        }
    }

    private void cacheData(File file, byte[] data) {
        byte[] salt = createSalt();
        SecretKeySpec originalKeySpec;
        try {
            originalKeySpec = generateKey(key, salt);

            byte[] iv = createIv();
            byte[] encrypted = encrypt(originalKeySpec, iv, data);
            saveToFile(file, encrypted, salt, iv);
            return;
        } catch (NoSuchAlgorithmException |
                InvalidKeySpecException |
                NoSuchPaddingException |
                InvalidKeyException |
                InvalidAlgorithmParameterException |
                IllegalBlockSizeException |
                BadPaddingException e) {
            e.printStackTrace();
        }
        Log.d("EncryptedDataSource", "cacheMessages :: Caching data failed!");
    }

    private byte[] getCachedData(File file) {
        if (!file.exists()) {
            Log.d("EncryptedDataSource", "getCachedData :: Nothing in cache for file: " + file);
            return new byte[0];
        }

        byte[] encryptedRetrieved;
        try {
            encryptedRetrieved = getFromFile(file);
            if (encryptedRetrieved.length < ENCRYPTION_KEY_LENGTH + ENCRYPTION_IV_LENGTH) {
                throw new IllegalArgumentException("Detected invalid cached file!");
            }

            int encryptedDataLength = encryptedRetrieved.length - (ENCRYPTION_KEY_LENGTH + ENCRYPTION_IV_LENGTH);
            byte[] retrievedData = Arrays.copyOfRange(encryptedRetrieved, 0, encryptedDataLength);
            byte[] retrievedSalt = Arrays.copyOfRange(encryptedRetrieved, encryptedDataLength, encryptedRetrieved.length - ENCRYPTION_IV_LENGTH);
            byte[] retrievedIv = Arrays.copyOfRange(encryptedRetrieved, encryptedRetrieved.length - ENCRYPTION_IV_LENGTH, encryptedRetrieved.length);
            SecretKeySpec newGenKeySpec = generateKey(key, retrievedSalt);

            return decrypt(newGenKeySpec, retrievedIv, retrievedData);
        } catch (NoSuchAlgorithmException |
                InvalidKeyException |
                InvalidAlgorithmParameterException |
                NoSuchPaddingException |
                BadPaddingException |
                InvalidKeySpecException |
                IllegalBlockSizeException e) {
            e.printStackTrace();
            Log.d("EncryptedDataSource", "getCachedMessages :: Failed to retrieve cached messages");
        }
        return new byte[0];
    }

    private List<Message> getCachedMessages() {
        byte[] decrypted = getCachedData(cacheFileMessages);
        if (decrypted.length > 0) {
            return Parley.getInstance().getNetwork().config.getJsonParser().jsonToMessages(new String(decrypted));
        } else {
            return new ArrayList<>();
        }
    }

    @Nullable
    private String getCachedValue(String key) {
        File destinationFile;
        switch (key) {
            case KEY_MESSAGE_INFO:
                destinationFile = cacheFileInfo;
                break;
            case KEY_PAGING:
                destinationFile = cacheFilePaging;
                break;
            default:
                Log.d("EncryptedDataSource", "cacheValue :: Unexpected value for key: " + key);
                return null;
        }
        byte[] data = getCachedData(destinationFile);
        if (data.length > 0) {
            return new String(data);
        } else {
            return null;
        }
    }

    @Override
    public void clear() {
        cacheFileMessages.delete();
        cacheFileInfo.delete();
        cacheFilePaging.delete();
    }

    @Override
    public List<Message> getAll() {
        return getCachedMessages();
    }

    @Override
    public void add(List<Message> messages) {
        List<Message> cached = getCachedMessages();
        cached.addAll(messages);
        cacheMessages(cached);
    }

    @Override
    public void add(int index, List<Message> messages) {
        List<Message> cached = getCachedMessages();
        cached.addAll(index, messages);
        cacheMessages(cached);
    }

    @Override
    public void add(Message message) {
        List<Message> cached = getCachedMessages();
        cached.add(message);
        cacheMessages(cached);
    }

    @Override
    public void add(int index, Message message) {
        List<Message> cached = getCachedMessages();
        cached.add(index, message);
        cacheMessages(cached);
    }

    @Override
    public void update(Message message) {
        List<Message> cachedMessages = getCachedMessages();
        Integer index = null;
        for (int i = 0; i < cachedMessages.size(); i++) {
            if (message.getUuid().equals(cachedMessages.get(i).getUuid())) {
                index = i;
                break;
            }
        }

        if (index != null) {
            cachedMessages.set(index, message);
            cacheMessages(cachedMessages);
        }
    }

    @Override
    public void set(String key, @Nullable String value) {
        cacheValue(key, value);
    }

    @Nullable
    @Override
    public String get(String key) {
        return getCachedValue(key);
    }
}

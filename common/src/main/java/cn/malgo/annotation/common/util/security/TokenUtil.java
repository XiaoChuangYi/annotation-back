package cn.malgo.annotation.common.util.security;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Date;

/**
 * Created by cjl on 2018/2/7.
 */
public class TokenUtil {
    /**
     * 生成java-web-token
     * @param id
     * @param issuer 签发者
     * @param subject 订阅者
     * @param ttlMillis 时间
     * @param apiSecret 密钥
     */
    public static String  generateJwt(String id,String issuer,String subject,long ttlMillis,String apiSecret){
        SignatureAlgorithm signatureAlgorithm=SignatureAlgorithm.HS256;
        long nowMillis=System.currentTimeMillis();
        Date now =new Date(nowMillis);
        byte [] apiKeySecretBytes= DatatypeConverter.parseBase64Binary(apiSecret);
        Key  signKey=new SecretKeySpec(apiKeySecretBytes,signatureAlgorithm.getJcaName());
        JwtBuilder builder= Jwts.builder().setId(id)
                .setIssuedAt(now)
                .setSubject(subject)
                .setIssuer(issuer)
                .signWith(signatureAlgorithm,signKey);
        if(ttlMillis>0){
            long expMillis=nowMillis+ttlMillis;
            Date exp=new Date(expMillis);
            builder.setExpiration(exp);
        }
        return builder.compact();
    }
    /**
     *解码和验证token
     * @param jwt 前端返回的字符串
     * @param apiSecret 密钥
     */
    public void parseJWT(String jwt,String apiSecret){
        Claims claims=Jwts.parser()
                .setSigningKey(DatatypeConverter.parseBase64Binary(apiSecret))
                .parseClaimsJws(jwt).getBody();
    }
}

package com.zzd.giligili.service.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.zzd.giligili.domain.exception.ConditionException;
import io.netty.util.internal.StringUtil;

import java.util.Calendar;
import java.util.Date;

/**
 * 使用JWT生成用户登录令牌
 * @author dongdong
 * @Date 2023/7/19 16:39
 */
public class TokenUtil {

    private static String ISSUER = "zhai";

    public static String generateToken(Long userId) throws Exception {
        Algorithm algorithm = Algorithm.RSA256(RSAUtil.getPublicKey(),RSAUtil.getPrivateKey());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MINUTE, 10);
        return JWT.create().withKeyId(String.valueOf(userId))
                .withIssuer(ISSUER)
                .withExpiresAt(calendar.getTime())
                .sign(algorithm);
    }

    public static String generateRefreshToken(Long userId) throws Exception {
        Algorithm algorithm = Algorithm.RSA256(RSAUtil.getPublicKey(),RSAUtil.getPrivateKey());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_MONTH, 7);
        return JWT.create().withKeyId(String.valueOf(userId))
                .withIssuer(ISSUER)
                .withExpiresAt(calendar.getTime())
                .sign(algorithm);
    }
    public static Long verifyToken(String token){
        if (StringUtil.isNullOrEmpty(token)) {
            throw new ConditionException("token参数异常!");
        }
        try {
            Algorithm algorithm = Algorithm.RSA256(RSAUtil.getPublicKey(),RSAUtil.getPrivateKey());
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT jwt = verifier.verify(token);
            String userId = jwt.getKeyId();
            return Long.valueOf(userId);
        } catch (TokenExpiredException e){
            throw new ConditionException("550","token过期！");
        } catch (Exception e) {
            throw new ConditionException("非法用户token！");
        }
    }
    public static Long verifyRefreshToken(String refreshToken) throws Exception {
        if (StringUtil.isNullOrEmpty(refreshToken)) {
            throw new ConditionException("token参数异常!");
        }
        try {
            Algorithm algorithm = Algorithm.RSA256(RSAUtil.getPublicKey(),RSAUtil.getPrivateKey());
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT jwt = verifier.verify(refreshToken);
            String userId = jwt.getKeyId();
            return Long.valueOf(userId);
        } catch (Exception e) {
            throw new ConditionException("555","token过期！");
        }

    }



}

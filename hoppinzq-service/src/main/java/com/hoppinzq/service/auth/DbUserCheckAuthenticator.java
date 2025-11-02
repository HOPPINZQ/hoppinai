package com.hoppinzq.service.auth;


import com.hoppinzq.service.annotation.InterfaceImplName;
import com.hoppinzq.service.common.HoppinInvocationRequest;
import com.hoppinzq.service.common.UserPrincipal;
import com.hoppinzq.service.exception.AuthenticationFailedException;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author:ZhangQi 使用JDBC数据源执行身份验证的验证器
 * 一般是通过数据库来校验用户是否存在，使用secretId，secretKey可以避免用户传输用户名密码被截获。
 * @see HoppinInvocationRequest#getCredentials() 通过该方法可以获取到调用方身份信息
 */
@InterfaceImplName("使用JDBC数据源执行身份验证的验证器")
public class DbUserCheckAuthenticator implements AuthenticationProvider {
    private DataSource dataSource;
    private PreparedStatement cachedAuthStatement;

    public DbUserCheckAuthenticator(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void authenticate(HoppinInvocationRequest hoppinInvocationRequest) throws AuthenticationFailedException {
        if (hoppinInvocationRequest.getCredentials() != null && hoppinInvocationRequest.getCredentials() instanceof UserPrincipal) {
            try {
                UserPrincipal upp = (UserPrincipal) hoppinInvocationRequest.getCredentials();
                PreparedStatement stmt = getAuthStatement();
                stmt.setString(0, upp.getUsername());
                stmt.setString(1, upp.getPassword());

                executeAndValidate(stmt, hoppinInvocationRequest);
            } catch (SQLException e) {
                cachedAuthStatement = null;
                e.printStackTrace();
                throw new AuthenticationFailedException(e);
            }
        }
    }

    public void executeAndValidate(PreparedStatement stmt, HoppinInvocationRequest hoppinInvocationRequest) throws SQLException, AuthenticationFailedException {
        if (stmt.executeQuery().next()) {
            AuthenticationContext.setPrincipal(hoppinInvocationRequest.getCredentials());
        } else {
            throw new AuthenticationFailedException("身份验证失败");
        }
    }

    private PreparedStatement getAuthStatement() throws SQLException {
        if (cachedAuthStatement == null) {
            cachedAuthStatement = dataSource.getConnection().prepareStatement(getAuthSql());
        }

        return cachedAuthStatement;
    }


    public String getAuthSql() {
        return "SELECT * FROM users WHERE username = ? AND password = ?";
    }

}

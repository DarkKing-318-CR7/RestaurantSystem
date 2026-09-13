package com.example.Restaurant.config;

public class TenantContext {
    //ThreadLocal đảm bảo mỗi request sẽ có một không gian bộ nhớ độc lập
    private static final ThreadLocal<Long> currentBranch =new ThreadLocal<>();

    public static void setCurrentBranch(Long branchId){
        currentBranch.set(branchId);
    }

    public static Long getCurrentBranch(){
        return currentBranch.get();
    }

    public static void clear()
        {
        currentBranch.remove();
        }
}

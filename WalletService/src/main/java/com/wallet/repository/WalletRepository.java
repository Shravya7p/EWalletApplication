package com.wallet.repository;

import com.wallet.model.Wallet;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletRepository extends JpaRepository<Wallet,Integer> {

    Wallet findByPhoneNo(String phoneNo);

    @Transactional
    @Modifying
    @Query("update Wallet w set w.balance = w.balance+:amount where w.phoneNo = :phoneNo")
    void updateWallet(String phoneNo,Double amount);

}

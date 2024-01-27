package com.driver.services;


import com.driver.EntryDto.SubscriptionEntryDto;
import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.repository.SubscriptionRepository;
import com.driver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.sql.Date;
import java.util.List;

@Service
public class SubscriptionService {

    @Autowired
    SubscriptionRepository subscriptionRepository;

    @Autowired
    UserRepository userRepository;

    public Integer buySubscription(SubscriptionEntryDto subscriptionEntryDto){

        //Save The subscription Object into the Db and return the total Amount that user has to pay
        Date subscriptionDate = Date.valueOf(LocalDate.now());
        int totalAmount=0;
        if(subscriptionEntryDto.getSubscriptionType().equals(SubscriptionType.BASIC))
            totalAmount = 500 + 200*subscriptionEntryDto.getNoOfScreensRequired();
        else if(subscriptionEntryDto.getSubscriptionType().equals(SubscriptionType.PRO))
            totalAmount = 800 + 250*subscriptionEntryDto.getNoOfScreensRequired();
        else
            totalAmount = 1000 + 350*subscriptionEntryDto.getNoOfScreensRequired();

        Subscription subscription = new Subscription(subscriptionEntryDto.getSubscriptionType(),
                subscriptionEntryDto.getNoOfScreensRequired(), subscriptionDate, totalAmount);

        subscriptionRepository.save(subscription);
        return totalAmount;
    }

    public Integer upgradeSubscription(Integer userId)throws Exception{

        //If you are already at an ElITE subscription : then throw Exception ("Already the best Subscription")
        //In all other cases just try to upgrade the subscription and tell the difference of price that user has to pay
        //update the subscription in the repository

        Subscription subscription = subscriptionRepository.findById(userId).get();
        SubscriptionType subscriptionType = subscription.getSubscriptionType();
        int getNoOfScreensSubscribed = subscription.getNoOfScreensSubscribed();
        int totalAmount=0;
        if(subscriptionType.equals(SubscriptionType.BASIC))
        {
            subscriptionType=SubscriptionType.PRO;
            totalAmount = 300 + 50 * getNoOfScreensSubscribed;
        } else if (subscriptionType.equals(SubscriptionType.ELITE)) {
            subscriptionType=SubscriptionType.ELITE;
            totalAmount = 200 + 100 * getNoOfScreensSubscribed;
        }
        else
            throw new Exception("Already the best Subscription");

        subscription.setSubscriptionType(subscriptionType);
        subscription.setTotalAmountPaid(subscription.getTotalAmountPaid()+totalAmount);
        subscriptionRepository.save(subscription);

        return totalAmount;
    }

    public Integer calculateTotalRevenueOfHotstar(){

        //We need to find out total Revenue of hotstar : from all the subscriptions combined
        //Hint is to use findAll function from the SubscriptionDb

        List<Subscription> subscriptionList = subscriptionRepository.findAll();
        int totalAmount=0;
        for(Subscription subscription : subscriptionList){
            totalAmount+=subscription.getTotalAmountPaid();
        }
        return totalAmount;
    }

}

package com.driver.services.impl;

import com.driver.model.*;
import com.driver.repository.CountryRepository;
import com.driver.repository.ServiceProviderRepository;
import com.driver.repository.UserRepository;
import com.driver.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository3;

    @Autowired
    ServiceProviderRepository serviceProviderRepository3;

    @Autowired
    CountryRepository countryRepository3;

    @Override
    public User register(String username, String password, String countryName) throws Exception{
        if(countryName.equalsIgnoreCase("ind") || countryName.equalsIgnoreCase("aus") || countryName.equalsIgnoreCase("usa") || countryName.equalsIgnoreCase("jpn") || countryName.equalsIgnoreCase("chi")) {
            User user = new User();
            user.setUsername(username);
            user.setPassword(password);

            Country country = new Country();

            if(countryName.equalsIgnoreCase("ind")){
                country.setCountryName(CountryName.IND);
                country.setCode(CountryName.IND.toCode());
            }

            if(countryName.equalsIgnoreCase("aus")){
                country.setCountryName(CountryName.AUS);
                country.setCode(CountryName.AUS.toCode());
            }

            if(countryName.equalsIgnoreCase("usa")){
                country.setCountryName(CountryName.USA);
                country.setCode(CountryName.USA.toCode());
            }

            if(countryName.equalsIgnoreCase("jpn")){
                country.setCountryName(CountryName.JPN);
                country.setCode(CountryName.JPN.toCode());
            }

            if(countryName.equalsIgnoreCase("chi")){
                country.setCountryName(CountryName.CHI);
                country.setCode(CountryName.CHI.toCode());
            }

            country.setUser(user);
            user.setOriginalCountry(country);
            user.setConnected(false);
            user.setMaskedIp(null);

            String OriginalIp = country.getCode() + "." + userRepository3.save(user).getId();
            user.setOriginalIp(OriginalIp);
            userRepository3.save(user);
            return user;
        }else{
            throw new Exception("Country not found");
        }
    }

    @Override
    public User subscribe(Integer userId, Integer serviceProviderId) {
        User user = userRepository3.findById(userId).get();
        ServiceProvider serviceProvider = serviceProviderRepository3.findById(serviceProviderId).get();

        user.getServiceProviderList().add(serviceProvider);
        serviceProvider.getUsers().add(user);

        serviceProviderRepository3.save(serviceProvider);
        return user;
    }
}

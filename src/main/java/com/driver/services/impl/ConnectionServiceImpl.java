package com.driver.services.impl;

import com.driver.model.Connection;
import com.driver.model.Country;
import com.driver.model.ServiceProvider;
import com.driver.model.User;
import com.driver.repository.ConnectionRepository;
import com.driver.repository.ServiceProviderRepository;
import com.driver.repository.UserRepository;
import com.driver.services.ConnectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConnectionServiceImpl implements ConnectionService {

    @Autowired
    UserRepository userRepository2;

    @Autowired
    ServiceProviderRepository serviceProviderRepository2;

    @Autowired
    ConnectionRepository connectionRepository2;

    @Override
    public User connect(int userId, String countryName) throws Exception{
        User user = userRepository2.findById(userId).get();

        if(user.getMaskedIp()!=null)
            throw new Exception("Already connected");
        else if (countryName.equalsIgnoreCase(user.getOriginalCountry().toString())) {
            return user;
        } else{
            if(user.getServiceProviderList()==null){
                throw new Exception("Unable to connect");
            }

            List<ServiceProvider> providers = user.getServiceProviderList();
            int min = Integer.MAX_VALUE;
            ServiceProvider serviceProvider1 = null;
            Country country1 = null;

            for(ServiceProvider serviceProvider:providers){
                List<Country> countryList = serviceProvider.getCountryList();

                for (Country country:countryList){

                    if(countryName.equalsIgnoreCase(country.getCountryName().toString()) && min>serviceProvider.getId()){
                        min=serviceProvider.getId();
                        serviceProvider1=serviceProvider;
                        country1=country;
                    }
                }
            }
            if(serviceProvider1!=null){
                Connection connection = new Connection();
                connection.setUser(user);
                connection.setServiceProvider(serviceProvider1);

                String countryCode = country1.getCode();
                int providerId = serviceProvider1.getId();
                String masked = countryCode + "." + providerId +"."+ userId;

                user.setMaskedIp(masked);
                user.setConnected(true);
                user.getConnectionList().add(connection);

                serviceProvider1.getConnectionList().add(connection);

                userRepository2.save(user);
                serviceProviderRepository2.save(serviceProvider1);

                return user;
            }
            else
                throw new Exception("Unable to connect");
        }
    }


    @Override
    public User disconnect(int userId) throws Exception {
        User user = userRepository2.findById(userId).get();
        if(!user.getConnected()){
            throw new Exception("Already disconnected");
        }
        else{

            user.setConnected(false);
            user.setMaskedIp(null);

            userRepository2.save(user);
            return user;
        }
    }


    @Override
    public User communicate(int senderId, int receiverId) throws Exception {
        return new User();
    }
}

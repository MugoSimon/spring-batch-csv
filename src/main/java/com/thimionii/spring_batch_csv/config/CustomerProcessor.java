package com.thimionii.spring_batch_csv.config;

import com.thimionii.spring_batch_csv.entity.Customer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.batch.item.ItemProcessor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CustomerProcessor implements ItemProcessor<Customer, Customer> {
    @Override
    public Customer process(Customer item) throws Exception {
        return item;
    }
}

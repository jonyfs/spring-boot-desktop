package br.com.jonyfs.message;

import org.springframework.data.repository.PagingAndSortingRepository;

public interface MessageRepository extends PagingAndSortingRepository<Message, Long> {

}

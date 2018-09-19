package com.pos.retailer.service.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.pos.retailer.model.SequenceGenerator;
import com.pos.retailer.repository.SequenceGeneratorRepository;
import com.pos.retailer.service.SequenceGeneratorService;

@Service
public class SequenceGeneratorServiceImpl implements SequenceGeneratorService {

	@Autowired
	SequenceGeneratorRepository sequenceGeneratorRepository;

	@Override
	public Long getNextValue(String name) {
		Long seq = 1L;

		SequenceGenerator seqGen = sequenceGeneratorRepository.findByName(name).orElse(null);
		if (ObjectUtils.isEmpty(seqGen)) {
			seqGen = new SequenceGenerator(name, seq);
		} else {
			seqGen.increase();
			seq = seqGen.getSeq();
		}

		sequenceGeneratorRepository.save(seqGen);

		return seq;
	}
}

package cn.edu.nju.njutcm.rna;

import cn.edu.nju.njutcm.rna.service.FileService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RnaApplicationTests {

	@Autowired
	FileService fileService;

	@Test
	public void contextLoads() {
		System.out.println(fileService.getByUser("test").size());
	}

}

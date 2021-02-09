package com.yxd.daily;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DailyApplicationTests {

	@Test
	public void contextLoads() {
//		System.out.println(DBUtil.getDay());
//		String s1 = "123";
//		String s2 = "124";
//		System.out.println(s1.compareTo(s2));
//
//		s1 = "125";
//		s2 = "125";
//		System.out.println(s1.compareTo(s2));
//
//		s1 = "126";
//		s2 = "125";
//		System.out.println(s1.compareTo(s2));
	}

	@Test
	public void cal() {

		String str = "abcsabced";

		String maxStr = getMax(str);

		//输出结果
		System.out.println("max="+maxStr);
	}

	/**
	 * 计算
	 * @param str
	 * @return
	 */
	static String getMax(String str) {

		Map<String, Integer> count = new HashMap<>();

		//和 冒泡算法 一样的 双重for循环
		for(int i=0; i<str.length(); i++) {
			for(int j=i+1; j< str.length()+1; j++) {

				//截取 i-j 个字符串
				String curr = str.substring(i,j);

				if(count.get(curr) == null) {
					count.put(curr, 1);
				} else {
					count.put(curr, count.get(curr)+1);
				}

			}

		}

		int maxSum = -1;
		String maxStr = "";
		Set<String> set = count.keySet();
		for(String curr : set) {

			if(count.get(curr) >= maxSum && curr.length() > maxStr.length()) {

				maxSum = count.get(curr);
				maxStr = curr;
			}
		}

		return maxStr;

	}

}

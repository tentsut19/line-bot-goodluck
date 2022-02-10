package com.cabsat.linebot;

import com.cabsat.linebot.client.request.CustomerRequest;
import com.cabsat.linebot.exception.CustomException;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import org.junit.runner.RunWith;
import org.springframework.util.StringUtils;

import java.util.Calendar;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RunWith(MockitoJUnitRunner.class)
class ApplicationTests {

	@Test
	void test(){
		Calendar cal1 = Calendar.getInstance(TimeZone.getTimeZone("Asia/Bangkok"));//GMT+7
		System.out.println(cal1.getTime());//11
	}

	@Test
	void test_createCustomerRequest(){
		String s = "no:17.;n:นางสาวสุวิมล นามบุรี;" +
				"a:740 หมู่บ้าน วี.เค โฮม ซอยอนามัยงามเจริญ 25 ถนน พระราม2 ท่าข้าม บางขุนเทียน กทม. 10150;" +
				"p:0840199611,0840199612;" +
				"so:F.Suwimol Namburi;" +
				"pt:Cod;" +
				"pr:949;(s:38;pro:หมีคอนเวิร์ส;c:ดำ;q:1;)(s:38;pro:หมีบาว;c:ขาว;q:6;)";

		System.out.println("=============== no ================");
		Pattern MY_PATTERN = Pattern.compile("no:(.*?);");
		Matcher m = MY_PATTERN.matcher(s);
		while (m.find()) {
			System.out.println(m.group(1));
		}
		System.out.println("=============== name ================");
		MY_PATTERN = Pattern.compile("n:(.*?);");
		m = MY_PATTERN.matcher(s);
		while (m.find()) {
			System.out.println(m.group(1));
		}
		System.out.println("=============== address ================");
		MY_PATTERN = Pattern.compile("a:(.*?);");
		m = MY_PATTERN.matcher(s);
		while (m.find()) {
			System.out.println(m.group(1));
		}
		System.out.println("=============== phone ================");
		MY_PATTERN = Pattern.compile("p:(.*?);");
		m = MY_PATTERN.matcher(s);
		while (m.find()) {
			System.out.println(m.group(1));
		}
		System.out.println("=============== social_name ================");
		MY_PATTERN = Pattern.compile("so:(.*?);");
		m = MY_PATTERN.matcher(s);
		while (m.find()) {
			System.out.println(m.group(1));
		}
		System.out.println("=============== payment_channel ================");
		MY_PATTERN = Pattern.compile("pt:(.*?);");
		m = MY_PATTERN.matcher(s);
		while (m.find()) {
			System.out.println(m.group(1));
		}
		System.out.println("=============== price ================");
		MY_PATTERN = Pattern.compile("pr:(.*?);");
		m = MY_PATTERN.matcher(s);
		while (m.find()) {
			System.out.println(m.group(1));
		}
		System.out.println("=============== order ================");
		MY_PATTERN = Pattern.compile("\\((.*?)\\)");
		Matcher m_order = MY_PATTERN.matcher(s);
		while (m_order.find()) {
			String order = m_order.group(1);
			System.out.println(order);
			System.out.println("=============== size ================");
			MY_PATTERN = Pattern.compile("s:(.*?);");
			m = MY_PATTERN.matcher(order);
			while (m.find()) {
				System.out.println(m.group(1));
			}
			System.out.println("=============== product_name ================");
			MY_PATTERN = Pattern.compile("pro:(.*?);");
			m = MY_PATTERN.matcher(order);
			while (m.find()) {
				System.out.println(m.group(1));
			}
			System.out.println("=============== color ================");
			MY_PATTERN = Pattern.compile("c:(.*?);");
			m = MY_PATTERN.matcher(order);
			while (m.find()) {
				System.out.println(m.group(1));
			}
			System.out.println("=============== quantity ================");
			MY_PATTERN = Pattern.compile("q:(.*?);");
			m = MY_PATTERN.matcher(order);
			while (m.find()) {
				System.out.println(m.group(1));
			}
		}

	}

	@Test
	void test_createCustomerRequest_v1(){
		String s = "ลำดับ:17.;ชื่อ:นางสาวสุวิมล นามบุรี;" +
				"ที่อยู่:740 หมู่บ้าน วี.เค โฮม ซอยอนามัยงามเจริญ 25 ถนน พระราม2 ท่าข้าม บางขุนเทียน กทม. 10150;" +
				"เบอร์:0840199611,0840199612;" +
				"ชื่อเฟส:F.Suwimol Namburi;" +
				"จ่าย:Cod;" +
				"ราคา:949;(ไซด์:38;สินค้า:หมีคอนเวิร์ส;สี:ดำ;จำนวน:1;)(ไซด์:38;สินค้า:หมีบาว;สี:ขาว;จำนวน:6;)";

		System.out.println("=============== no ================");
		Pattern MY_PATTERN = Pattern.compile("ลำดับ:(.*?);");
		Matcher m = MY_PATTERN.matcher(s);
		while (m.find()) {
			System.out.println(m.group(1));
		}
		System.out.println("=============== name ================");
		MY_PATTERN = Pattern.compile("ชื่อ:(.*?);");
		m = MY_PATTERN.matcher(s);
		while (m.find()) {
			System.out.println(m.group(1));
		}
		System.out.println("=============== address ================");
		MY_PATTERN = Pattern.compile("ที่อยู่:(.*?);");
		m = MY_PATTERN.matcher(s);
		while (m.find()) {
			System.out.println(m.group(1));
		}
		System.out.println("=============== phone ================");
		MY_PATTERN = Pattern.compile("เบอร์:(.*?);");
		m = MY_PATTERN.matcher(s);
		while (m.find()) {
			System.out.println(m.group(1));
		}
		System.out.println("=============== social_name ================");
		MY_PATTERN = Pattern.compile("ชื่อเฟส:(.*?);");
		m = MY_PATTERN.matcher(s);
		while (m.find()) {
			System.out.println(m.group(1));
		}
		System.out.println("=============== payment_channel ================");
		MY_PATTERN = Pattern.compile("จ่าย:(.*?);");
		m = MY_PATTERN.matcher(s);
		while (m.find()) {
			System.out.println(m.group(1));
		}
		System.out.println("=============== price ================");
		MY_PATTERN = Pattern.compile("ราคา:(.*?);");
		m = MY_PATTERN.matcher(s);
		while (m.find()) {
			System.out.println(m.group(1));
		}
		System.out.println("=============== order ================");
		MY_PATTERN = Pattern.compile("\\((.*?)\\)");
		Matcher m_order = MY_PATTERN.matcher(s);
		while (m_order.find()) {
			String order = m_order.group(1);
			System.out.println(order);
			System.out.println("=============== size ================");
			MY_PATTERN = Pattern.compile("ไซด์:(.*?);");
			m = MY_PATTERN.matcher(order);
			while (m.find()) {
				System.out.println(m.group(1));
			}
			System.out.println("=============== product_name ================");
			MY_PATTERN = Pattern.compile("สินค้า:(.*?);");
			m = MY_PATTERN.matcher(order);
			while (m.find()) {
				System.out.println(m.group(1));
			}
			System.out.println("=============== color ================");
			MY_PATTERN = Pattern.compile("สี:(.*?);");
			m = MY_PATTERN.matcher(order);
			while (m.find()) {
				System.out.println(m.group(1));
			}
			System.out.println("=============== quantity ================");
			MY_PATTERN = Pattern.compile("จำนวน:(.*?);");
			m = MY_PATTERN.matcher(order);
			while (m.find()) {
				System.out.println(m.group(1));
			}
		}

	}

	@Test
	void test_createCustomerRequest_v1_success() throws CustomException {
		LineBotController lineBotController = new LineBotController();

		String text1 = "ภิชญาภาภรณ์  มีบริบูรณ์  \n" +
				"กองปฏิบัติการดับและกู้ภัย 6 77/1 ถนนพระรามที่6 แขวงทุ่งพญาไท เขตราชเทวี กรุงเทพ 10400\n" +
				"0816669216-0816669215-0816669217\n" +
				"F.นางภิชญาภาภรณ์  มีบริบูรณ์  \n" +
				"โอน-468\n" +
				"โลชั่น-ชมพู 2 \n" +
				"โลชั่น-ขาว 3 \n" +
				"รองเท้าเข็มขัดชมพู 1 ชมพู 40\n" +
				"รองเท้าเข็มขัดครีม 2 ครีม 38";

		String textEdit = "OD01111-ภิชญาภาภรณ์  มีบริบูรณ์  \n" +
				"กองปฏิบัติการดับและกู้ภัย 6 77/1 ถนนพระรามที่6 แขวงทุ่งพญาไท เขตราชเทวี กรุงเทพ 10400\n" +
				"0816669216-0816669215-0816669217\n" +
				"F.นางภิชญาภาภรณ์  มีบริบูรณ์  \n" +
				"โอน-468\n" +
				"โลชั่น-ชมพู 2 \n" +
				"โลชั่น-ขาว 3 \n" +
				"รองเท้าเข็มขัดชมพู 1 ชมพู 40\n" +
				"รองเท้าเข็มขัดครีม 2 ครีม 38";

		String textError = "18307.โศรดา ฟ้ากระจ่าง\n" +
				"327ม.1ต.บางแก้ว อ.บางพลี\n" +
				"จ.สมุทรปราการ 10540 0619730891\n" +
				"F. โซรดา ฟ้ากระจ่าง \n" +
				"Cod-258\n" +
				"แตะเกาหลี35ดำ 1";


		CustomerRequest customerRequest = lineBotController.createCustomerRequest_v1(textEdit);
		System.out.println(customerRequest);
	}

	@Test
	void test_v3(){
		String input = "3433. อาทิตยา แขขุนทด";
		String regex = "\\d+.";
		System.out.println(input.replaceAll(regex,"").trim());
	}

	@Test
	void test_regex_date(){
		String date = "สรุปยอดวันที่ 01/12/2019-02/12/2019";
		String regex = "^(.*?)(1[0-2]|0[1-9])/(3[01]|[12][0-9]|0[1-9])/[0-9]{4}-(1[0-2]|0[1-9])/(3[01]|[12][0-9]|0[1-9])/[0-9]{4}$";
		//Creating a pattern object
		Pattern pattern = Pattern.compile(regex);
		//Matching the compiled pattern in the String
		Matcher matcher = pattern.matcher(date);
		boolean bool = matcher.matches();
		if(bool) {
			System.out.println("Date is valid");
//			regex = "^.*(1[0-2]|0[1-9])/(3[01]|[12][0-9]|0[1-9])/[0-9]{4}$";
//			pattern = Pattern.compile(regex);
			matcher = pattern.matcher(date);
			if (matcher.find())
			{
//				System.out.println(matcher.group(0));
				date = date.replaceAll(matcher.group(1),"");
				System.out.println(date);
			}

		} else {
			System.out.println("Date is not valid");
		}
	}

	@Test
	public void test_1234(){
//		String input = "รองเท้าSup38ดำ,รองเท้าSup38เขียว,รองเท้าSup38ขาว,รองเท้าSup39ขาว";
		String input = "รองเท้า";
		String regex = "\\d+.";
		String[] ss = input.split(regex);
		if(ss.length > 0){
			System.out.println(ss[0]);
		}
	}

	@Test
	public void test_12345(){
		String input = "รองเท้าSupดำ,รองเท้าSup38เขียว,รองเท้าSup38ขาว,รองเท้าSup39ขาว";
//		String input = "รองเท้า";
		String regex = "\\d+.";
		String[] ss = input.split(regex);
		if(ss.length > 0){
			input = ss[0];
			System.out.println(ss[0]);
		}
		regex = "\\,+.";
		ss = input.split(regex);
		if(ss.length > 0){
			System.out.println(ss[0]);
		}
	}

}

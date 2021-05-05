# ip2nation_web_v0.4.py

# -*- coding: utf-8 -*-

import sys
import requests
from bs4 import BeautifulSoup
import re

text_str = []

if len(sys.argv) > -1:
	filename = "target_korex.txt"
	with open(filename, 'r') as f:
		for line in f:
			text_str.append(line.rstrip())

w = open('result_find_kr_20190830.csv', 'w')

for a in text_str:
	string_input = a
	######## IP verification #######
	p = re.compile('^\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}$')  # IP Format
	m = p.search(a)

	p2 = re.compile('0\.0\.0\.0|127\.0\.0\.1|10\..*|172\.(1[6-9]|2[0-9]|3[0|1])\..*|192\.168\..*')  # 0.0.0.0, 127.0.0.1, 10.0.0.0 ~ 10.255.255.255, 172.16.0.0 ~ 172.31.255.255, 192.168.0.0 ~ 192.168.255.255
	m2 = p2.search(a)

	if m:  # IP Format Check
		if m2 is None:  # 'None' is not private IP Address

			URL = "http://www.ip2nation.com/"
			session = requests.Session()
			headers = {
				"Host": "www.ip2nation.com",
				"User-Agent": "User-Agent: Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.100 Safari/537.36",
				"Origin": "www.ip2nation.com",
				"Accept": "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3",
				"Connection": "close"
			}

			data = {'ip': string_input}
			response = requests.post(URL, data=data)
			string_html = BeautifulSoup(response.content, "html.parser")
			country = string_html.find("acronym")

			print(a, "Address Country  : ", country.get_text())
			w.write(a + ",Address Country," + country.get_text() + "\n")

		else:
			pass
	else:
		pass

w.close()

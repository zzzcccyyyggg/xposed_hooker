import json
# 初始化一个空字典来存放应用名称和包名
config_dict = {}
app_dict = {}
# 使用with打开文件，自动处理文件关闭
with open('data.txt', 'r',encoding='utf-8') as file:
    # 读取文件的所有行
    lines = file.readlines()
    # 遍历每行来寻找应用名称和包名
    for line in lines:
        # 如果行中包含"App Name"和"Package Name"，那么这行定义了一个应用及其包名
        if "App Name:" in line and "Package Name:" in line:
            # 分割行来获取应用名称和包名
            parts = line.split(',')
            # 进一步分割来提取具体的名称和包名
            app_name_part, package_name_part = parts[0].strip(), parts[1].strip()
            # 去掉前缀"App Name: "和"Package Name: "
            app_name = app_name_part.replace("App Name: ", "").strip()
            package_name = package_name_part.replace("Package Name: ", "").strip()
            # 将这些添加到字典中
            app_dict[app_name] = package_name
        if "ssid:" in line:
            config_dict['ssid'] = line.strip().replace("ssid: ","").strip()
        if "bssid:" in line:
            config_dict['bssid'] = line.strip().replace("bssid: ","").strip()
        if "SIM card:" in line:
            config_dict['SIM card'] = line.strip().replace("SIM card: ","").strip()
        if "imei:" in line:
            config_dict['imei'] = line.strip().replace("imei: ","").strip()
        if "meid:" in line:
            config_dict['meid'] = line.strip().replace("meid: ","").strip()
        if "Original Android ID:" in line:
            config_dict['Original Android ID:'] = line.strip().replace("Original Android ID: ","").strip()
        if "MAC Address:" in line:
            config_dict['MAC Address'] = line.strip().replace("MAC Address: ","").strip()
        if "serialNumber:" in line:
            config_dict['serialNumber'] = line.strip().replace("serialNumber: ","").strip()

    config_dict['package'] = app_dict

# 打印结果
json.dump(config_dict,open('result2.txt', 'w',encoding='utf-8'),ensure_ascii=False,indent=4)
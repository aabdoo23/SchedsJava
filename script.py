from bs4 import BeautifulSoup
import time
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC


def initialize_driver():
    chromedriver_path = "chromedriver.exe"
    options = webdriver.ChromeOptions()
    options.add_argument('--headless')
    options.add_argument('--window-size=1920x1080')
    driver = webdriver.Chrome(executable_path=chromedriver_path, options=options)
    return driver


def login(driver, username, password):
    url = 'https://register.nu.edu.eg/PowerCampusSelfService/Home'
    driver.get(url)

    user_name = WebDriverWait(driver, 5).until(EC.element_to_be_clickable((By.CSS_SELECTOR, "input[type='text']")))
    user_name.clear()
    print("Sending user name")
    user_name.send_keys(username)
    time.sleep(0.5)

    next_btn = WebDriverWait(driver, 5).until(EC.element_to_be_clickable((By.CSS_SELECTOR, "button[type='button']")))
    print("Click Next")
    next_btn.click()
    time.sleep(0.5)

    password_field = WebDriverWait(driver, 5).until(
        EC.element_to_be_clickable((By.CSS_SELECTOR, "input[type='password']")))
    password_field.clear()
    print("Sending Password")
    password_field.send_keys(password)
    time.sleep(0.5)

    sign_in_btn = WebDriverWait(driver, 5).until(EC.element_to_be_clickable((By.ID, "btnSignIn")))
    print("Click Sign In")
    sign_in_btn.click()
    time.sleep(0.5)


def navigate_to_courses(driver):
    print("Going to Courses")
    registration_url = 'https://register.nu.edu.eg/PowerCampusSelfService/Registration/Courses'
    driver.get(registration_url)
    time.sleep(0.5)
    advanced_search = WebDriverWait(driver, 5).until(
        EC.element_to_be_clickable((By.CSS_SELECTOR, "button[type='button']")))
    advanced_search.click()
    time.sleep(0.5)
    search_btn = WebDriverWait(driver, 5).until(EC.element_to_be_clickable((By.ID, "btnSearch")))
    search_btn.click()


def scrape_course_info(driver):
    print("Fetching Courses Data")
    courses_div_element = WebDriverWait(driver, 15).until(
        EC.visibility_of_element_located((By.XPATH, '//*[@id="contentPage"]/div[2]/div/div/div[1]/div[3]/div'))
    )
    courses_div = courses_div_element.get_attribute('innerHTML')
    soup = BeautifulSoup(courses_div, "lxml")

    all_courses = soup.find_all('div', class_='jss569 grid-item jss571 jss524 jss615 jss568 jss621 jss650')

    course_info_list = []

    for course in all_courses:
        time.sleep(1)
        course_name = course.find('h4').find('span').text.strip()
        section_details = course.find_all('span', class_='jss426 jss413 jss768 jss761 jss429 jss425 jss455')[
            0].text.strip()

        course_link = course.find('h4').find('button')
        button_id = course_link['id']
        driver.find_element_by_id(button_id).click()

        popup = WebDriverWait(driver, 15).until(
            EC.visibility_of_element_located((By.XPATH, '//*[@id="sectionDetailModal"]/div[3]/div'))
        )

        schedules = WebDriverWait(driver, 15).until(
            EC.visibility_of_element_located(
                (By.XPATH, '//*[@id="sectionDetailModal"]/div[3]/div/div[2]/div[5]/div')
            )
        ).find_elements_by_tag_name('div')

        instructor_name_div = WebDriverWait(driver, 10).until(
            EC.presence_of_element_located(
                (By.XPATH, '/html/body/div[2]/div[3]/div/div[2]/div[3]/div/div/div[2]')
            )
        )
        instructor_name = instructor_name_div.find_element_by_tag_name('p').text.strip()

        schedule_paragraphs_list = []
        for schedule in schedules:
            all_p_tags = schedule.find_elements_by_tag_name('p')
            for p_tag in all_p_tags:
                if p_tag.text.strip() not in schedule_paragraphs_list:
                    schedule_paragraphs_list.append(p_tag.text.strip())

        course_info = {
            "Instructor Name": instructor_name,
            "Course Name": course_name,
            "Section Details": section_details,
            "Schedule": schedule_paragraphs_list
        }
        course_info_list.append(course_info)

        close_button = popup.find_element_by_class_name('dialog-close-button')
        close_button.click()
        WebDriverWait(driver, 5).until(
            EC.invisibility_of_element_located((By.XPATH, '//*[@id="sectionDetailModal"]/div[3]/div'))
        )

    return course_info_list


def save_course_info_to_file(course_info_list):
    with open('course_info.txt', 'w', encoding='utf-8') as file:
        for course_info in course_info_list:
            file.write("Instructor Name: " + course_info["Instructor Name"] + "\n")
            file.write("Course Name: " + course_info["Course Name"] + "\n")
            file.write(course_info["Section Details"] + "\n")
            file.write("Schedule:" + "\n")
            for course_schedule in course_info["Schedule"]:
                file.write(course_schedule + "\n")
            file.write("---------\n")


def main():
    try:
        user_name = input("Enter your Username: ")
        pass_key = input("Enter your Password: ")

        driver = initialize_driver()
        login(driver, user_name, pass_key)
        time.sleep(2)
        navigate_to_courses(driver)
        course_info_list = scrape_course_info(driver)
        save_course_info_to_file(course_info_list)

        print("Mission Accomplished")
        driver.quit()
        print("Browser closed.")
    except Exception as e:
        driver.quit()
        print(f"An error Occurred Due To: \n{e}")


if __name__ == "__main__":
    main()

## TÊN DỰ ÁN: MONOGATARI APP (ANDROID MOBILE APPLICATION)

### 1. KIẾN TRÚC TỔNG THỂ HỆ THỐNG

- **Mô hình**: Client-Server. Tập trung phát triển Android Mobile Client làm giao diện tương tác chính, kết nối với Spring Boot Backend thông qua RESTful API.

- **Xóa bỏ rào cản mạng (LAN Bypass)**: Sử dụng Ngrok làm Reverse Proxy tạo Public HTTPS URL. Giải pháp này giúp app Android có thể truy cập API từ bất kỳ đâu (Wifi công cộng, 4G/5G) để test thực tế, đồng thời mở cổng an toàn để nhận Webhook từ hệ thống thanh toán quốc tế.

### 2. ANDROID MOBILE CLIENT (TRỌNG TÂM ĐỒ ÁN)

- **Kiến trúc ứng dụng**: Áp dụng chuẩn **MVVM (Model - View - ViewModel)**.

    - Tách biệt hoàn toàn UI (View) và Business Logic (ViewModel/Repository).

    - Giúp app giữ được trạng thái dữ liệu (state) khi người dùng chuyển đổi cấu hình.

- **Giao tiếp & Xác thực**: Sử dụng Retrofit để ánh xạ và gọi API.

    - Tích hợp đăng nhập nhanh qua **Google (OAuth2)**, mang lại trải nghiệm Onboarding mượt mà cho người dùng di động mà không cần tự quản lý mật khẩu.

### 3. BACKEND & SECURITY (SPRING BOOT)

Hệ thống máy chủ được thiết kế theo kiến trúc **Monolithic 3-Layers**, tập trung vào tính ổn định và khả năng mở rộng.

- **Cơ chế Xác thực (Stateful JWT)**: Kế thừa sự gọn nhẹ của JWT để dễ dàng truyền tải qua HTTP Header trên Mobile.

    - Tuy nhiên, hệ thống thiết kế JWT theo hướng Stateful (kiểm tra trạng thái tại server) để có khả năng **Revoke (thu hồi)** token ngay lập tức, cho phép Force Logout thiết bị di động khi cần thiết – một yêu cầu bảo mật bắt buộc đối với các app có thanh toán.

- **Hệ thống Notification (API-Ready)**: Backend đã xây dựng sẵn luồng logic đẩy thông báo dựa trên quan hệ `Follow` của User và `Story`. (Trong khuôn khổ đồ án, tính năng này được demo thông qua việc trigger trực tiếp các Admin API bằng công cụ như Postman/Swagger).

### 4. TÍCH HỢP DỊCH VỤ ĐÁM MÂY (CLOUD INTEGRATIONS)

- **Thanh toán Premium (Stripe)**: Xử lý Subscription an toàn. Hệ thống sử dụng Webhook qua Ngrok để tự động lắng nghe kết quả thanh toán từ Stripe và đồng bộ quyền lợi Premium cho User.

- Trợ lý AI (Google Gemini): Tích hợp tính năng Chatbot "The Archivist" hỗ trợ người dùng tóm tắt truyện. Hệ thống được tinh chỉnh để xử lý khéo léo các lỗi từ API bên thứ ba (như Rate Limit) mà không làm crash ứng dụng Android.

### 5. CƠ SỞ DỮ LIỆU (DATABASE ERD & MYSQL)
Sử dụng MySQL kết hợp Spring Data JPA và Flyway để quản lý 3 cụm dữ liệu cốt lõi:

1. **User & Security**: `User`, `Subscription`, `Transaction` (Xác thực và thanh toán).

2. **Content Hub**: `Story`, `Author`, `Genre`, `Chapter`, `ChapterImage` (Kho lưu trữ Manga/Novel).

3. **Social & Tracking**: `ReadingProgress` (Lưu mốc đọc truyện), `Follow`, `Rating`, `Comment`, `Notification`.

### 6. Hệ thống lọc nội dung theo độ tuổi (Age-Filtering)

- **Cơ chế hoạt động**: Ứng dụng tự động ẩn hoặc hiển thị truyện dựa trên sự đối chiếu giữa tuổi người dùng ($User\_Age$) và giới hạn độ tuổi của truyện ($Story.age\_limit$).

- **Ràng buộc người dùng**: Ngay sau khi đăng nhập lần đầu (kể cả qua Google OAuth2), nếu hệ thống kiểm tra thấy thông tin ngày sinh còn trống, người dùng **bắt buộc** phải hoàn thiện hồ sơ tại màn hình **Edit Profile**.

    - Đây là bước "Onboarding" bắt buộc để kích hoạt bộ lọc nội dung, đảm bảo trải nghiệm đọc truyện an toàn và đúng đối tượng ngay từ đầu.

- **Mục tiêu**: Đảm bảo tính an toàn nội dung và cá nhân hóa kho truyện theo đúng định danh độ tuổi của từng tài khoản.

### 7. Hệ thống Thanh toán & Phân quyền Nội dung (Stripe Integration)
- **Mô hình nội dung (Freemium)**:

    - **Free Chapters**: Cho phép người dùng trải nghiệm miễn phí một số chương đầu của mỗi bộ truyện (ví dụ 3 chương đầu).

    - **Premium Chapters**: Các chương tiếp theo được gắn nhãn `is_premium`. Để truy cập, người dùng bắt buộc phải có gói **Subscription** (đăng ký theo tháng/năm) còn hiệu lực.

- **Cơ chế thanh toán qua Stripe**:

    - Sử dụng **Stripe API** để xử lý giao dịch an toàn và chuyên nghiệp.

    - Hệ thống tự động đồng bộ trạng thái gói cước thông qua **Webhook**, đảm bảo quyền lợi Premium được kích hoạt ngay lập tức sau khi thanh toán thành công.

- **Logic quản lý vòng đời Subscription**:

    - **Duy trì quyền truy cập**: Người dùng có quyền hủy gói đăng ký bất cứ lúc nào để tránh việc tự động gia hạn (tự động trừ tiền kỳ sau).

    - **Chế độ chờ kết thúc (Grace Period)**: Sau khi bấm hủy, hệ thống **không cắt quyền ngay lập tức** mà vẫn cho phép người dùng duy trì trạng thái Premium cho đến ngày cuối cùng của chu kỳ thanh toán hiện tại (`current_period_end`).

    - **Tự động chấm dứt**: Khi hết chu kỳ, nếu người dùng đã hủy, gói cước sẽ chính thức hết hạn và quyền truy cập vào các chương Premium sẽ bị khóa lại.

- **Trải nghiệm trên Android**:

    - Tại danh sách chương, ứng dụng hiển thị biểu tượng "Khóa" đối với các nội dung Premium.

    - Khi người dùng nhấn vào chương khóa, App sẽ kiểm tra trạng thái Subscription và điều hướng sang màn hình thanh toán nếu cần thiết.

### 8. CÁC VẤN ĐỀ KỸ THUẬT & HƯỚNG GIẢI QUYẾT (KNOWN ISSUES)
Nhóm đã xác định được một số thách thức và lỗi kỹ thuật tồn đọng trong quá trình phát triển và kiểm thử thực tế:

- **Lỗi điều hướng đăng nhập lần đầu (Onboarding Redirection Bug)**:

    - **Hiện trạng**: Theo thiết kế, người dùng mới (đặc biệt là qua OAuth2) phải được điều hướng đến màn hình nhập ngày sinh để kích hoạt bộ lọc nội dung. Tuy nhiên, hiện tại luồng điều hướng đôi khi bị bỏ qua, cho phép người dùng vào thẳng trang chủ (`MainActivity`) khi chưa cập nhật thông tin này.

    - **Hệ quả**: Khi ngày sinh bị trống (`null`), hệ thống mặc định tuổi người dùng là 0. Do cơ chế **Age-Based Filtering**, trang chủ sẽ lọc bỏ toàn bộ các truyện có giới hạn độ tuổi, dẫn đến việc không hiển thị bất kỳ nội dung nào. Điều này gây hiểu lầm cho người dùng là ứng dụng bị lỗi dữ liệu hoặc không có nội dung.

    - **Hướng xử lý**: Nhóm đang tinh chỉnh lại logic kiểm tra trạng thái hồ sơ ngay tại `SplashActivity` và sử dụng `Navigation Component` để khóa chặt luồng di chuyển cho đến khi dữ liệu ngày sinh được ghi nhận thành công.

- **Đồng bộ trạng thái đọc truyện (ReadingProgress Resume Bug)**:

    - **Hiện trạng**: Backend đã ghi nhận chính xác vị trí đọc cuối cùng (số trang) vào cơ sở dữ liệu. Tuy nhiên, phía Mobile Client gặp độ trễ trong việc đồng bộ giữa luồng tải ảnh (`Image Loading`) và lệnh cuộn trang (`Scroll`).

    - **Hệ quả**: Ứng dụng đôi khi không thể tự động nhảy đến đúng trang đã lưu mà bị trả về trang đầu tiên của chương truyện.

    - **Hướng xử lý**: Nghiên cứu cách sử dụng `callback` sau khi danh sách ảnh đã render hoàn hành để thực thi lệnh cuộn trang chính xác hơn.

- **Giới hạn lưu lượng AI (AI Quota Limit)**:

- **Hiện trạng**: Do sử dụng gói miễn phí của Google Gemini, hệ thống dễ gặp lỗi **429 (Too Many Requests)** khi có lượng yêu cầu liên tục từ phía người dùng.

- **Giải pháp**: Nhóm đã triển khai bộ phận xử lý ngoại lệ (`Exception Handling`) tại Mobile Client để hiển thị thông báo "Hệ thống đang bận" một cách thân thiện, thay vì gây treo hoặc văng ứng dụng (crash).
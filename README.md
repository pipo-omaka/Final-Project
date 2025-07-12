📌 Simple Poll/Survey API -

แอปนี้เป็น RESTful API สำหรับสร้างและจัดการโพลล์ (Polls) และตัวเลือก (PollOptions) พร้อมฟังก์ชันโหวต

✅ Endpoint หลักที่มี:
- POST /polls : สร้างโพลล์ใหม่
- GET /polls : ดูรายการโพลล์ทั้งหมด
- GET /polls/{id} : ดูโพลล์พร้อมตัวเลือกและจำนวนโหวต
- PUT /polls/{id} : แก้ไขคำถามโพลล์
- DELETE /polls/{id} : ลบโพลล์

- POST /options : เพิ่มตัวเลือกให้โพลล์
- PUT /options/{id} : แก้ไขตัวเลือก
- POST /options/{id}/vote : โหวตให้ตัวเลือกนั้น (เพิ่ม voteCount)

🧪 สามารถทดสอบได้จากไฟล์ test.http ที่แนบมา

📦 Server รันที่ `http://localhost:8080`

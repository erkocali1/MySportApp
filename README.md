# MySportApp
Running App with Dependency Injection

Developed in Kotlin, this application is developed using Google Maps services and Dagger Hilt injection framework. The application offers the ability to monitor the distance the user has walked and the calories burned. In this way, the user can track their health and fitness goals.

<div style="display: flex; align-items: center; justify-content: center; flex-direction: column;">
  <img src="https://github.com/erkocali1/ss/blob/master/app/src/main/res/drawable/gif.gif" width="350" height="550">
  <p id="loadingText" style="text-align: center; font-size: 16px;">Yükleniyor...</p>
  <div style="width: 200px; height: 20px; background-color: #eee; border-radius: 10px;">
    <div id="progressBar" style="width: 0%; height: 100%; background-color: #4287f5; border-radius: 10px;"></div>
  </div>
</div>

<script>
  // İlerleme çubuğunu güncellemek için JavaScript kullanıyoruz
  var progressBar = document.getElementById('progressBar');
  var loadingText = document.getElementById('loadingText');
  
  // İlerleme çubuğunun değerini güncellemek için bir fonksiyon
  function updateProgress(progress) {
    progressBar.style.width = progress + '%';
  }
  
  // İlerleme çubuğunu animasyonlu olarak güncellemek için bir döngü
  var progress = 0;
  var interval = setInterval(function() {
    progress += 10;
    updateProgress(progress);
    
    if (progress >= 100) {
      clearInterval(interval);
      loadingText.textContent = 'Tamamlandı';
    }
  }, 500);
</script>


 
  <h1>   Structures I use in my app: </h1>
   <p>
-Navigation Component <br>
-MVVM, UI/UX design <br>
-Dependency Injection(Dagger Hilt) <br>
-Room <br>
-Google Maps Service <br>
-Navigation Component <br>
-SharedPrefences <br>
-Recycler View  <br>
   <p>

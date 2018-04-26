from django.urls import include, path
from django.contrib import admin

urlpatterns = [
    path('Authenticate/', include('google_auth.urls')),
    path('admin/', admin.site.urls),
]

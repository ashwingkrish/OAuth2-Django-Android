from django.urls import path
from . import views

urlpatterns = [
    path('google_calendar/', views.google_calendar, name='google_calendar'),
    path('gmail/', views.gmail, name='gmail'),
    path('gmail_readonly/', views.gmail_readonly, name='gmail_readonly'),
    path('drive/', views.drive, name='drive'),
    path('drive_readonly/', views.drive_readonly, name='drive_readonly'),
]

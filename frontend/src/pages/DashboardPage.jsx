import { Container, Grid, Paper, Typography, Box } from '@mui/material';

function DashboardPage() {
  return (
    <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
      <Typography variant="h4" gutterBottom>
        PortPilot24 대시보드
      </Typography>
      <Grid container spacing={3}>
        {/* Card for Container Monitoring */}
        <Grid item xs={12} md={6} lg={4}>
          <Paper sx={{ p: 2, display: 'flex', flexDirection: 'column', height: 240 }}>
            <Typography variant="h6" color="primary" gutterBottom>컨테이너 모니터링</Typography>
            <Typography>실시간 컨테이너 위치 및 상태 추적</Typography>
          </Paper>
        </Grid>
        {/* Card for Safety Detection */}
        <Grid item xs={12} md={6} lg={4}>
          <Paper sx={{ p: 2, display: 'flex', flexDirection: 'column', height: 240 }}>
             <Typography variant="h6" color="primary" gutterBottom>안전보호구 감지</Typography>
             <Typography>CCTV 영상 분석을 통한 실시간 안전 경고</Typography>
          </Paper>
        </Grid>
         {/* Card for Document Analysis */}
        <Grid item xs={12} md={6} lg={4}>
          <Paper sx={{ p: 2, display: 'flex', flexDirection: 'column', height: 240 }}>
             <Typography variant="h6" color="primary" gutterBottom>세관 서류 검수</Typography>
             <Typography>AI 기반 서류 자동 분류 및 검토</Typography>
          </Paper>
        </Grid>
      </Grid>
    </Container>
  );
}

export default DashboardPage;
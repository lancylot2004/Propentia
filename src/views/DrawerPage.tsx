import { useTheme } from "@mui/material/styles";
import Box from "@mui/material/Box";
import Drawer from "@mui/material/Drawer";
import CssBaseline from "@mui/material/CssBaseline";
import Divider from "@mui/material/Divider";

const drawerWidth = 240;

interface DrawerPageProps {
  children: React.ReactNode
  open: boolean
}

export default function DrawerPage(props: DrawerPageProps) {
  useTheme();
  const open = props.open;

  return (
    <Box sx={{ display: "flex" }}>
      <CssBaseline />
      <Drawer
        sx={{
          width: drawerWidth,
          flexShrink: 0,
          "& .MuiDrawer-paper": {
            width: drawerWidth,
            boxSizing: "border-box",
          },
        }}
        variant="persistent"
        anchor="left"
        open={open}
      >
        Propentia
        <Divider />
      </Drawer>
      {props.children}
    </Box>
  );
}

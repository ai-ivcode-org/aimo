import * as React from 'react';
import { styled, useTheme, Theme, CSSObject } from '@mui/material/styles';
import Box from '@mui/material/Box';
import MuiDrawer from '@mui/material/Drawer';
import MuiAppBar, { AppBarProps as MuiAppBarProps } from '@mui/material/AppBar';
import Toolbar from '@mui/material/Toolbar';
import List from '@mui/material/List';
import CssBaseline from '@mui/material/CssBaseline';
import Typography from '@mui/material/Typography';
import Divider from '@mui/material/Divider';
import IconButton from '@mui/material/IconButton';
import MenuIcon from '@mui/icons-material/Menu';
import ChevronLeftIcon from '@mui/icons-material/ChevronLeft';
import ChevronRightIcon from '@mui/icons-material/ChevronRight';
import ListItemButton from '@mui/material/ListItemButton';
import ListItemIcon from '@mui/material/ListItemIcon';
import ListItemText from '@mui/material/ListItemText';
import TryIcon from '@mui/icons-material/Try';
import HistoryIcon from '@mui/icons-material/History';
import {Collapse, Tooltip} from "@mui/material";
import { historyService, HistoryEntry } from "../../services/history-service/HistoryService";
import { ChatSessionSingleton } from '../../services/chat-service/ChatSession';
import {useEffect} from "react";

const drawerWidth = 260;

const openedMixin = (theme: Theme): CSSObject => ({
    width: drawerWidth,
    transition: theme.transitions.create('width', {
        easing: theme.transitions.easing.sharp,
        duration: theme.transitions.duration.enteringScreen,
    }),
    overflowX: 'hidden',
});

const closedMixin = (theme: Theme): CSSObject => ({
    transition: theme.transitions.create('width', {
        easing: theme.transitions.easing.sharp,
        duration: theme.transitions.duration.leavingScreen,
    }),
    overflowX: 'hidden',
    width: `calc(${theme.spacing(7)} + 1px)`,
    [theme.breakpoints.up('sm')]: {
        width: `calc(${theme.spacing(8)} + 1px)`,
    },
});

const DrawerHeader = styled('div')(({ theme }) => ({
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'flex-end',
    // necessary for content to be below app bar
    ...theme.mixins.toolbar,
}));

interface AppBarProps extends MuiAppBarProps {
    open?: boolean;
}

const AppBar = styled(MuiAppBar, {
    shouldForwardProp: (prop) => prop !== 'open',
})<AppBarProps>(({ theme }) => ({
    zIndex: theme.zIndex.drawer + 1,
    transition: theme.transitions.create(['width', 'margin'], {
        easing: theme.transitions.easing.sharp,
        duration: theme.transitions.duration.leavingScreen,
    }),
    variants: [
        {
            props: ({ open }) => open,
            style: {
                marginLeft: drawerWidth,
                width: `calc(100% - ${drawerWidth}px)`,
                transition: theme.transitions.create(['width', 'margin'], {
                    easing: theme.transitions.easing.sharp,
                    duration: theme.transitions.duration.enteringScreen,
                }),
            },
        },
    ],
}));

const Drawer = styled(MuiDrawer, { shouldForwardProp: (prop) => prop !== 'open' })(
    ({ theme }) => ({
        width: drawerWidth,
        flexShrink: 0,
        whiteSpace: 'nowrap',
        boxSizing: 'border-box',
        variants: [
            {
                props: ({ open }) => open,
                style: {
                    ...openedMixin(theme),
                    '& .MuiDrawer-paper': openedMixin(theme),
                },
            },
            {
                props: ({ open }) => !open,
                style: {
                    ...closedMixin(theme),
                    '& .MuiDrawer-paper': closedMixin(theme),
                },
            },
        ],
    }),
);

interface SideDrawerProps {
    children?: React.ReactNode
}

export default function SideDrawer({ children }: SideDrawerProps) {
    const theme = useTheme();

    const [open, setOpen] = React.useState(false);
    const [historyOpen, setHistoryOpen] = React.useState(false);
    const [sessionId, setSessionId] = React.useState<string | null>( ChatSessionSingleton.id );

    const [historyItems, setHistoryItems] = React.useState<HistoryEntry[]>([]);

    useEffect(() => {
        return historyService.subscribe( items => {
            setHistoryItems(items);
        })
    }, []);
    useEffect(() => {
        return ChatSessionSingleton.onChange(async (id: string | null) => {
            setSessionId(id);
        })
    }, []);

    const handleDrawerOpen = () => {
        setOpen(true);
    };

    const handleDrawerClose = () => {
        setOpen(false);
    };

    return (
        <Box sx={{ display: 'flex' }}>
            <CssBaseline />
            <AppBar position="fixed" open={open}>
                <Toolbar style={{ minHeight: theme.mixins.toolbar.minHeight }}>
                    <IconButton
                        color="inherit"
                        aria-label="open drawer"
                        onClick={handleDrawerOpen}
                        edge="start"
                        sx={[
                            {
                                marginRight: 5,
                            },
                            open && { display: 'none' },
                        ]}
                    >
                        <MenuIcon />
                    </IconButton>
                    <Typography variant="h6" noWrap component="div">
                        Aimo
                    </Typography>
                </Toolbar>
            </AppBar>
            <Drawer variant="permanent" open={open}>
                <DrawerHeader style={{ minHeight: theme.mixins.toolbar.minHeight }}>
                    <IconButton onClick={handleDrawerClose}>
                        {theme.direction === 'rtl' ? <ChevronRightIcon /> : <ChevronLeftIcon />}
                    </IconButton>
                </DrawerHeader>
                <Divider />
                <List
                    sx={{ width: '100%', maxWidth: 360, bgcolor: 'background.paper' }}
                    component="nav"
                    aria-labelledby="nested-list-subheader"
                >
                    <Tooltip title={"New Chat"} placement="right" disableHoverListener={open} disableFocusListener={open}>
                        <ListItemButton onClick={ async () => { await ChatSessionSingleton.clear(false) } }>
                            <ListItemIcon>
                                <TryIcon />
                            </ListItemIcon>
                            <ListItemText primary="New Chat" />
                        </ListItemButton>
                    </Tooltip>
                </List>
                <Divider />
                <List
                    sx={{ width: '100%', maxWidth: 360, bgcolor: 'background.paper' }}
                    component="nav"
                    aria-labelledby="nested-list-subheader"
                >
                    { open ? (
                        <ListItemButton onClick={() => {
                            setHistoryOpen(!historyOpen)
                        }}>
                            <ListItemIcon>
                                <HistoryIcon />
                            </ListItemIcon>
                            <ListItemText primary="History" />
                        </ListItemButton>
                    ) : (
                        <Tooltip title={"History"} placement="right">
                            <ListItemButton onClick={() => {
                                setOpen(true);
                                setHistoryOpen(true);
                            }}>
                                <ListItemIcon>
                                    <HistoryIcon />
                                </ListItemIcon>
                                <ListItemText primary="History" />
                            </ListItemButton>
                        </Tooltip>
                    )}
                    <Collapse in={open && historyOpen} timeout="auto" unmountOnExit>
                        {historyItems.map(item => (
                            <ListItemButton
                                key={item.id}
                                sx={{ pl: 2 }}
                                onClick={ async () => { await ChatSessionSingleton.setId(item.id, false) } }
                                selected={sessionId === item.id}
                            >
                                <ListItemText
                                    primary={item.title ?? `Item ${item.id}`}
                                />
                            </ListItemButton>
                        ))}
                    </Collapse>
                </List>
            </Drawer>
            <Box component="main" sx={{ flexGrow: 1, p: 0}} >
                {children}
            </Box>
        </Box>
    );
}
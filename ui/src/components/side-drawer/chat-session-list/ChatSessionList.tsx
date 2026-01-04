import React, {useEffect} from 'react';
import {chatSession} from "../../../services/chat-session-service/ChatSession";
import {HistoryEntry, historyService} from "../../../services/history-service/HistoryService";
import {Button, ButtonGroup, Collapse, List, ListItemButton, ListItemIcon, ListItemText, Tooltip} from "@mui/material";
import {History as HistoryIcon, DeleteForever as DeleteForeverIcon, Edit as EditIcon} from "@mui/icons-material";
import {chatClient} from "../../../services/chat-client/ChatClient";

export interface ChatSessionListProps {
    drawerOpen?: boolean;
    onOpenDrawer?: () => void;
}

export default function ChatSessionList(props: ChatSessionListProps) {
    const [open, setOpen] = React.useState(false);
    const [historyItems, setHistoryItems] = React.useState<HistoryEntry[]>([]);
    const [sessionId, setSessionId] = React.useState<string | null>( chatSession.id );
    const [hoveredId, setHoveredId] = React.useState<string | null>(null);

    useEffect(() => {
        // when historyService updates, update local state
        return historyService.subscribe( items => {
            setHistoryItems(items);
        })
    }, []);
    useEffect(() => {
        // when chatSession updates, update local state
        return chatSession.onChange(async (id: string | null) => {
            setSessionId(id);
        })
    }, []);

    const onDeleteSession = async (id: string) => {
        await chatClient.deleteChatSession(id);
        void historyService.fetchHistory()

        if(sessionId === id) {
            await chatSession.clear(false)
        }
    }

    return (
        <List
            sx={{
                width: '100%',
                maxWidth: 360,
                bgcolor: 'background.paper'
            }}
            component="nav"
            aria-labelledby="nested-list-subheader"
        >
            { props.drawerOpen ? (
                <ListItemButton onClick={() => {
                    setOpen(!open)
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
                        if(!props.drawerOpen && props.onOpenDrawer) {
                            props.onOpenDrawer();
                        }
                    }}>
                        <ListItemIcon>
                            <HistoryIcon />
                        </ListItemIcon>
                        <ListItemText primary="History" />
                    </ListItemButton>
                </Tooltip>
            )}
            <Collapse
                in={open && props.drawerOpen}
                timeout="auto"
                unmountOnExit
            >
                {historyItems.map(item => (
                    <ListItemButton
                        key={item.id}
                        sx={{ pl: 2, pr: "2px" }}
                        onClick={ async () => { await chatSession.setId(item.id, false) } }
                        selected={sessionId === item.id}
                        onMouseEnter={() => setHoveredId(item.id)}
                        onMouseLeave={() => setHoveredId(null)}
                    >
                        <ListItemText
                            primary={item.title ?? `Item ${item.id}`}
                        />
                        {(hoveredId === item.id && open) && (
                            <ButtonGroup
                                size="small"
                                variant="text"
                                fullWidth={false}
                            >
                                <Tooltip title={"Edit Title"} placement="top" enterDelay={500}>
                                    <Button
                                        size="small"
                                        style={{
                                            minWidth: "30px",
                                            border: 'none',
                                            margin: 0,
                                            opacity: "65%"
                                        }}
                                        onClick={async (e: React.MouseEvent<HTMLButtonElement>) => {
                                            e.stopPropagation();
                                            e.preventDefault();
                                        }}
                                    >
                                        <EditIcon/>
                                    </Button>
                                </Tooltip>

                                <Tooltip title={"Delete"} placement="top" enterDelay={500} >
                                    <Button
                                        size="small"
                                        style={{
                                            minWidth: "30px",
                                            border: 'none',
                                            margin: 0,
                                            opacity: "65%"
                                        }}
                                        onClick={async (e: React.MouseEvent<HTMLButtonElement>) => {
                                            e.stopPropagation();
                                            e.preventDefault();
                                            await onDeleteSession(item.id);
                                        }}
                                    >
                                        <DeleteForeverIcon/>
                                    </Button>
                                </Tooltip>
                            </ButtonGroup>
                        )}
                    </ListItemButton>
                ))}
            </Collapse>
        </List>
    )
}